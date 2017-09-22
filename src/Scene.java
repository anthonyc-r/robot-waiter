import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.texture.*;
import com.jogamp.opengl.util.texture.awt.*;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.awt.GLCanvas;

import java.util.ArrayList;
import java.util.Random;
import java.io.File;
import java.awt.Graphics2D;
import java.awt.image.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.awt.Color;
import javax.imageio.*;

public class Scene {
	private GLU glu = new GLU();
    private GLUT glut = new GLUT();
	
	private WavefrontObject room;
	private Texture roomTex;
    private AnimatedRobotWaiter robot;
    private Table table;
	private Camera camera;
	
	private Light spot1;
	private Light spot2;
	private Light worldLight;
	private float[] spotPosition1;
	private float[] spotDirection1;
	private float[] spotPosition2;
	private float[] spotDirection2;
	
	private double time;
	private boolean lighting;
	private boolean animation;
	private boolean robotView;
	
    private static final float[] DEFAULT_MAT = {1f, 1f, 1f};
    private static final float[] TABLE_DIFFUSE_MAT = {0.54f, 0.36f, 0.15f};
	private static final float[] TABLE_AMBIENT_MAT = {0.14f, 0.01f, 0f};
	
    /**
	* Scene constructor
	*/
    public Scene(GL2 gl, Camera camera) {
		//Load assets
        room = new WavefrontObject("../assets/obj/room.obj"); 
		roomTex = loadTexture("../assets/img/room_texture.jpg");
        table = new Table(6);
		robot = new AnimatedRobotWaiter("../assets/keyframes/keyframes2.xml", GL2.GL_LIGHT3);
		//init general
		this.camera = camera;
		time = 0;
		animation = true;
		lighting = true;
		robotView = false;

		//init lighting		
		initLighting(gl);
    }
    
	/**
	* update
	* Updates all time based objects within the scene.
	*/
    public void update() {
		//returns to start after 100.
		//inc by some factor
		if(animation){
			time += 1/5.0;
			time = time%99;
			robot.updateStateCurve(time);
		}
	}

	/**
	* render
	* Renders all objects within the scene.
	* @param gl The openGL2 context.
	*/
    public void render(GL2 gl) {
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();
		if(!robotView){
			camera.view(glu);
		}
		else{
			setRobotView(glu);
		}
		//Set position of lighting after camera, but before objects.
		doLighting(gl);

		//robot is a light source
		//Draw the robot only if viewing externally, still want 
		//the light though, so just temp cull all faces.
		if(robotView){
			gl.glCullFace(GL2.GL_FRONT_AND_BACK);
			gl.glPushMatrix();
			  gl.glTranslated(robot.getState().getXLocation(), 0, robot.getState().getZLocation());
			  robot.drawRobot(gl, glu, glut);
			gl.glPopMatrix();
			gl.glCullFace(GL2.GL_BACK);
		}
		else{
			gl.glPushMatrix();
			  gl.glTranslated(robot.getState().getXLocation(), 0, robot.getState().getZLocation());
			  robot.drawRobot(gl, glu, glut);
			gl.glPopMatrix();
		}
		
		//Draw the room
        gl.glPushMatrix();
		  drawRoom(gl);
        gl.glPopMatrix();
        
		//Draw the tables
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, TABLE_AMBIENT_MAT, 0);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, TABLE_DIFFUSE_MAT, 0);
        gl.glPushMatrix();
		  gl.glTranslated(-10, 0, -7);
		  table.draw(gl, glu);
        gl.glPopMatrix();
		gl.glPushMatrix();
		  gl.glTranslated(10, 0, 10);
		  table.draw(gl, glu);
		gl.glPopMatrix();
		gl.glPushMatrix();
		  gl.glTranslated(-10 ,0 , 7);
		  table.draw(gl, glu);
		gl.glPopMatrix();
		
        //drawAxes(gl, 5);
    }
	
	public void toggleLighting(){
		lighting = !lighting;
	}
	public void toggleAnimation(){
		animation = !animation;
	}
	public void toggleRobotView(){
		robotView = !robotView;
	}
	public void resetAnimation(){
		time = 0;
	}


    /**
    * Draws some simple line axes in order to aid testing.
	* @param gl The OpenGL2 context.
	* @param length The length of the axis.
    */
    private void drawAxes(GL2 gl, double length) {

        gl.glLineWidth(4);
        gl.glBegin(GL2.GL_LINES);
		  //x
          gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, new float[]{1f, 0f, 0f}, 0);
          gl.glColor3d(1, 0, 0);
          gl.glVertex3d(0, 0, 0);
          gl.glVertex3d(length, 0, 0);
		  
		  //y
		  gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, new float[]{0f, 1f, 0f}, 0);
		  gl.glColor3d(0, 1, 0);
		  gl.glVertex3d(0, 0, 0);
		  gl.glVertex3d(0, length, 0);
		  
		  //z
		  gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, new float[]{0f, 0f, 1f}, 0);
		  gl.glColor3d(0, 0, 1);
		  gl.glVertex3d(0, 0, 0);
		  gl.glVertex3d(0, 0, length);
        gl.glEnd();
        gl.glLineWidth(1);
		gl.glColor3d(1, 0, 0);

    }
	
	private void drawRoom(GL2 gl) {
		float[] ambient = new float[]{0.1f, 0.1f, 0.1f};
		float[] diffuse = new float[]{0.5f, 0.5f, 0.5f};
		//float[] specular = new float[]{0f, 0f, 0f};
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, ambient, 0);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, diffuse, 0);
		roomTex.enable(gl);
        roomTex.bind(gl);
        roomTex.setTexParameteri(gl, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
        roomTex.setTexParameteri(gl, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
        roomTex.setTexParameteri(gl, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);
		room.draw(gl);
		roomTex.disable(gl);
	}

    /**
    * loadTexture based on Tex4Scene.loadTexture by Steve
    * Maddock.
    **/
    private Texture loadTexture(String filename) {
        Texture tex = null;

        try {
            File f = new File("../assets/"+filename);
            BufferedImage img = ImageIO.read(f);    
            ImageUtil.flipImageVertically(img);
            tex = AWTTextureIO.newTexture(GLProfile.getDefault(),
                    img, false);
        }
        catch(Exception e) {
            System.out.println("Error loading texture:"+filename);
        }
        return tex;
    }
	
	/**
	* initLighting
	* initialises the scene lighting
	**/
	private void initLighting(GL2 gl) {	
		//Make room dark and moody
		float[] modelAmbient = new float[]{0.3f, 0.3f, 0.3f};
		gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, modelAmbient, 0);

		spotPosition1 = new float[]{-20f, 10f, -15f, 1f};
		spotDirection1 = new float[]{2f, -2.25f, 2f};
		spot1 = new Light(gl, GL2.GL_LIGHT0);
		spot1.setPosition(gl, spotPosition1);
		spot1.setSpotlight(gl, spotDirection1, 20f);
		
		spotPosition2 = new float[]{20f, 10f, 15f, 1f};
		spotDirection2 = new float[]{-2f, -2.25f, -2f};
		spot2 = new Light(gl, GL2.GL_LIGHT1);
		spot2.setPosition(gl, spotPosition2);
		spot2.setSpotlight(gl, spotDirection2, 20f);
		
		worldLight = new Light(gl, GL2.GL_LIGHT2);
	}
	
	/**
	* fixLighting
	* call at end of render to keep certain lighting objects in a fixed position
	**/
	private void doLighting(GL2 gl) {
		//Set spotlight direction and position to keep it constant.
		spot1.setPosition(gl, spotPosition1);
		spot1.setDirection(gl, spotDirection1);
		spot2.setPosition(gl, spotPosition2);
		spot2.setDirection(gl, spotDirection2);
		
		if(lighting){
			gl.glEnable(GL2.GL_LIGHT2);
		}
		else{
			gl.glDisable(GL2.GL_LIGHT2);
		}
	}
	
	private void setRobotView(GLU glu) {
		double robotX = robot.getState().getXLocation();
		double robotY = 4.1;
		double robotZ = robot.getState().getZLocation();
		//Push forward so the eye isn't rendered
		double fromX = robotX+0.3*Math.cos(Math.toRadians(robot.getFacingRotation()-90));
		double fromY = robotY;
		double fromZ = robotZ+0.3*Math.sin(Math.toRadians(robot.getFacingRotation()-90));
		//move even further out to get another point on the line
		double toX = robotX+2*Math.cos(Math.toRadians(robot.getFacingRotation()-90));
		double toY = robotY;
		double toZ = robotZ+2*Math.sin(Math.toRadians(robot.getFacingRotation()-90));
		glu.gluLookAt(fromX, fromY, fromZ, toX, toY, toZ, 0, 1, 0);
	}
		
}
