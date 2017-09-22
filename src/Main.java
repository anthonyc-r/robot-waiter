import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.*;

import java.awt.*;
import java.awt.event.*;

public class Main extends Frame implements GLEventListener, MouseMotionListener, ActionListener {
	private GLCanvas canvas;
    private Scene scene;
	private Camera camera;
    
    private int viewDistance;
	
	//Mouse movement
	private Point lastpoint;
	private int width, height;

    public static final double CAM_SPEED = 0.00628;
    public static final float FAR_CLIP = 100.0f;
    public static final float NEAR_CLIP = 0.1f;
    public final static int WIDTH = 800;
    public final static int HEIGHT = 600;
    public final static String TITLE = "Robo Waiter";

    public static void main(String[] args) {
        Main entry = new Main();
        entry.setVisible(true);
    }

    public Main() {
        setTitle(TITLE);   
        setSize(WIDTH, HEIGHT);
        viewDistance = 10;

		camera = new Camera(0, 0, 10);
		
        GLProfile glp = GLProfile.getDefault();
        GLCapabilities caps = new GLCapabilities(glp);
        canvas = new GLCanvas(caps);
        add(canvas, "Center");
        canvas.addGLEventListener(this);
        canvas.addMouseMotionListener(this);

        FPSAnimator animator = new FPSAnimator(canvas, 30);
        animator.start();
		
		addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
		
		Panel menuPanel = new Panel(new GridLayout(4, 1));
		
		Button toggleAnim = new Button("Toggle Animation");
		toggleAnim.setActionCommand("toggle animation");
		toggleAnim.addActionListener(this);
		Button resetAnim = new Button("Reset Animation");
		resetAnim.setActionCommand("reset animation");
		resetAnim.addActionListener(this);
		Button toggleLights = new Button("Toggle Lights");
		toggleLights.setActionCommand("toggle lights");
		toggleLights.addActionListener(this);
		Button toggleView = new Button("Toggle View");
		toggleView.setActionCommand("toggle view");
		toggleView.addActionListener(this);
		
		menuPanel.add(toggleAnim);
		menuPanel.add(resetAnim);
		menuPanel.add(toggleLights);
		menuPanel.add(toggleView);
		
		this.add("East", menuPanel);
    }

    /*----------------------------GLEventListener Interface------------------------*/

    /**
    * Enable needed features
    **/
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        //Enable checking z buffer to draw correctly
        gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glFrontFace(GL2.GL_CCW);
        //Dont draw back faces
        gl.glEnable(GL2.GL_CULL_FACE);
		gl.glCullFace(GL2.GL_BACK);
        //Set undrawn face to back
		gl.glEnable(GL2.GL_NORMALIZE);
		gl.glEnable(GL2.GL_LIGHTING);
        //interpolate vert color
        gl.glShadeModel(GL2.GL_SMOOTH);
        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
		
        scene = new Scene(gl, camera);
    }

    /**
    * Reshape written by Steve Maddock
    **/
    public void reshape(GLAutoDrawable drawable, int x, int y, 
            int width, int height) {
        GL2 gl = drawable.getGL().getGL2();

        this.width=width;
        this.height=height;
    
        float fAspect=(float) width/height;
        float fovy=60.0f;

        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
    
        float top=(float) Math.tan(Math.toRadians(fovy*0.5))*NEAR_CLIP;
        float bottom=-top;
        float left=fAspect*bottom;
        float right=fAspect*top;
    
        gl.glFrustum(left, right, bottom, top, 
                NEAR_CLIP, FAR_CLIP);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
    }

    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        scene.update();
        scene.render(gl);
    }

    public void dispose(GLAutoDrawable drawable) {

    }

	/*=============================MouseMotionListener INTERFACE==========================*/
	/**
	* mouseDragged by Steve Madock
	* The mouse is used to control the camera position.
	* @param e instance of MouseEvent, automatically supplied by the system when the user drags the mouse
	**/    
	public void mouseDragged(MouseEvent e) {
		Point ms = e.getPoint();
    
		float dx=(float) (ms.x-lastpoint.x)/width;
		float dy=(float) (ms.y-lastpoint.y)/height;
	
		if (e.getModifiers()==MouseEvent.BUTTON1_MASK) {
			camera.updateThetaPhi(-dx*2.0f, dy*2.0f);
		}
		else if (e.getModifiers()==MouseEvent.BUTTON3_MASK) {
			camera.updateRadius(-dy*10.0f);
		}
		lastpoint = ms;
	}
	
	/**
	* mouseMoved by Steve Maddock
	* The mouse is used to control the camera position.
	* @param e  instance of MouseEvent, automatically supplied by the system when the user moves the mouse
	**/  
	public void mouseMoved(MouseEvent e) {   
		lastpoint = e.getPoint(); 
	}
	
	/*==================================ActionListener Interface=================================*/
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equalsIgnoreCase("toggle animation")){
			scene.toggleAnimation();
		}
		else if(e.getActionCommand().equalsIgnoreCase("reset animation")){
			scene.resetAnimation();
		}
		else if(e.getActionCommand().equalsIgnoreCase("toggle lights")){
			scene.toggleLighting();
		}
		else if(e.getActionCommand().equalsIgnoreCase("toggle view")){
			scene.toggleRobotView();
		}
	}
	
	
}
