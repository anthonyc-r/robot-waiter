import com.jogamp.opengl.*; 
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.gl2.GLUT;

import java.util.ArrayList;
import java.util.Iterator;


public class RobotWaiter {
	//RobotState class defined at bottom.
	private State state;
	private double facingRotation;
	private double leanRotation;
	private Light eyeBeam;
	private int eyeBeamIndex;
	
	//Materials
	private static final float[] ROBOT = {0.1f, 0.1f, 0.1f};
	private static final float[] ROBOT_SPEC = {1f, 1f, 1f};
    private static final float[] PLATTER = {1f, 0.2f, 0.5f};
	private static final float[] ROBOT_BALL = {1f, 1f, 1f};
	private static final float[] EYE = {1f, 1f, 0f};
	private static final float[] EYE_EMISSION = {1f, 1f, 0f, 1f};
	private static final float[] DEFAULT_EMISSION = {0f, 0f, 0f, 1f};
	private static final float[] DEFAULT_SPECULAR = {0f, 0f, 0f, 1f};
    private static final float[] OTHER_MAT = {1f, 0.2f, 0.5f};

	/**
	* RobotWaiter
	* Draws a static robot waiter.
	**/
	public RobotWaiter(double xLocation, double yLocation, double forwardLean, int eyeBeamIndex) {
		state = new State(0, xLocation, yLocation);
		facingRotation = 0;
		leanRotation = 0;
		this.eyeBeamIndex = eyeBeamIndex;
	}

    public void drawRobot(GL2 gl, GLU glu, GLUT glut) {
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, ROBOT, 0);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, ROBOT_SPEC, 0);
		gl.glMateriali(GL2.GL_FRONT, GL2.GL_SHININESS, 4);
        gl.glPushMatrix();
		  //Rotate to facing direction.
		  gl.glRotated(-facingRotation, 0, 1, 0);
		  //Positive x axis = facing 0
		  //set lean amount
		  gl.glRotated(leanRotation, -1, 0, 0);
		  gl.glRotated(180, 0, 1, 0);
          //Body, arms.
          gl.glPushMatrix();
            //body main
            gl.glPushMatrix();
              //translate to local coord
              gl.glTranslated(0, 1, 0);
              //animate
              //--draw main body
              drawBody(gl , glu);
            gl.glPopMatrix();

            //left arm
            gl.glPushMatrix();
              //translate to local coord
              gl.glTranslated(0.5, 3.4, 0);
              //animate
              gl.glRotated(45, 0, 0, -1);
              //--draw larm
              drawLArmSeg(gl, glut);
              //lower left arm
              gl.glPushMatrix();
                //translate to local coord
                gl.glTranslated(0.8, 0, 0);
                //animate
				gl.glRotated(90, 0, 0, 1);
                //--draw lower larm
                drawLArmSeg(gl, glut);
                //left arm claws
                gl.glPushMatrix();
				  //translate to local coord
				  gl.glTranslated(0.7, 0, 0);
				  //animate
                  drawLClaws(gl, glut);
				  //platter
				  gl.glRotated(45, 0, 0, -1);
				  drawPlatter(gl, glu);
                gl.glPopMatrix();
              gl.glPopMatrix();
            gl.glPopMatrix();

            //right arm
            gl.glPushMatrix();
              //translate to local coord
              gl.glTranslated(-0.5, 3.4, 0);
              //animate
              gl.glRotated(45, 0, 0, 1);
              //--draw rarm
              drawRArmSeg(gl, glut);
              //lower right arm
              gl.glPushMatrix();
                //translate to local coord
                gl.glTranslated(-0.8, 0.0, 0);
                //animate
                //--draw lower right arm
                drawRArmSeg(gl, glut);
                //right arm claws
                gl.glPushMatrix();
			  	  //translate to local coord
			  	  gl.glTranslated(-0.7, 0, 0);
			  	  //animate
			  	  drawRClaws(gl, glut);
                gl.glPopMatrix();
              gl.glPopMatrix();
            gl.glPopMatrix();
          gl.glPopMatrix();

          //Legs = Sphere
          gl.glPushMatrix();
            //translate to local coord--already in place
            //animate
            //--draw robot sphere
            drawBall(gl, glu);
          gl.glPopMatrix();

          //Head
          gl.glPushMatrix();
            //translate to local
            gl.glTranslated(0, 3.5, 0);
            //animate
            //--draw head
            drawHead(gl, glu);
          gl.glPopMatrix();
        gl.glPopMatrix();        
		
		//Return to defaults
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, DEFAULT_SPECULAR, 0);
		gl.glMateriali(GL2.GL_FRONT, GL2.GL_SHININESS, 0);
    }

	/**
	* drawLClaw
	* Draws the claw of the left arm
	**/
    private void drawLClaws(GL2 gl, GLUT glut) {
		gl.glPushMatrix();
		  //Lower part
		  gl.glPushMatrix();
		    gl.glRotated(-45, 0, 0, 1);
            gl.glScaled(2, 1, 1);
            gl.glTranslated(0.1, 0, 0);
            glut.glutSolidCube(0.1f);
		  gl.glPopMatrix();
		  //upper part
          gl.glPushMatrix();
            gl.glScaled(2, 1, 1);
            gl.glTranslated(0.1, 0, 0);
            glut.glutSolidCube(0.1f);
          gl.glPopMatrix();
		gl.glPopMatrix();
    }

	/** drawRClaw
	* Draws the claw of the right arm
	**/
    private void drawRClaws(GL2 gl, GLUT glut) {
        gl.glPushMatrix();
		  //Lower part
		  gl.glPushMatrix();
		    gl.glRotated(45, 0, 0, 1);
            gl.glScaled(2, 1, 1);
            gl.glTranslated(-0.1, 0, 0);
            glut.glutSolidCube(0.1f);
		  gl.glPopMatrix();
		  //Upper part
		  gl.glPushMatrix();
            gl.glScaled(2, 1, 1);
            gl.glTranslated(-0.1, 0, 0);
            glut.glutSolidCube(0.1f);
		  gl.glPopMatrix();
        gl.glPopMatrix();
    }

	/** drawRArmSeg
	* Draws the one segment of the right arm
	**/
    private void drawRArmSeg(GL2 gl, GLUT glut) {
        gl.glPushMatrix();
        //Center of rotation at end of arm seg
        //cube no texutre
        gl.glScaled(8, 1, 2);
        gl.glTranslated(-0.05, 0, 0);
        glut.glutSolidCube(0.1f);
        gl.glPopMatrix();
    }

	/** drawRArmSeg
	* Draws the one segment of the left arm
	**/
    private void drawLArmSeg(GL2 gl, GLUT glut) {
        gl.glPushMatrix();
        gl.glScaled(8, 1, 2);
        gl.glTranslated(0.05, 0, 0);
        glut.glutSolidCube(0.1f);
        gl.glPopMatrix();
    }
	
	/**
	* drawPlatter
	* Draws the robot's platter
	**/
	public void drawPlatter(GL2 gl, GLU glu) {
		gl.glPushMatrix();
		  gl.glTranslated(1, 0, 0);
		  //Objects on platter
		    gl.glPushMatrix();
			  gl.glTranslated(0, 0.1, 0);
			  gl.glScaled(0.2, 0.4, 0.2);
			  SimpleObjects.drawFilledCylinder(gl, glu);
			gl.glPopMatrix();
		  //platter
		  gl.glScaled(2, 0.1, 2);
		  SimpleObjects.drawFilledCylinder(gl, glu);
		gl.glPopMatrix();
	}
	
	/** drawBody
	* Draws the robots body
	**/
    private void drawBody(GL2 gl, GLU glu) {
        gl.glPushMatrix();
		  gl.glScaled(1, 2.5, 1);
		  SimpleObjects.drawFilledCylinder(gl, glu);
        gl.glPopMatrix();
    }

	/** drawBall
	* Draws the ball the robot's body sits upon
	**/
    private void drawBall(GL2 gl, GLU glu) {
        //Join to body@(0, 1, 0)
        //Center of rotation at base of sphere.
        //sphere
        GLUquadric quadric = glu.gluNewQuadric();
        glu.gluQuadricDrawStyle(quadric, GLU.GLU_FILL);
        glu.gluQuadricTexture(quadric, false);
        glu.gluQuadricNormals(quadric, GLU.GLU_SMOOTH);
		
        gl.glPushMatrix();
          gl.glTranslated(0, 0.5, 0);
          glu.gluSphere(quadric, 0.5, 20, 20);
        gl.glPopMatrix();
    }

	/** drawHead
	* Draws the robot's head
	**/
    private void drawHead(GL2 gl, GLU glu) {
        gl.glPushMatrix();
        //Join to body@(0, 0, 0)
        //Center of rotation at base of neck
        //Cylinder stacked on a sphere, rotates from base of
        //sphere.
        GLUquadric quadric = glu.gluNewQuadric();
        glu.gluQuadricDrawStyle(quadric, GLU.GLU_FILL);
        glu.gluQuadricTexture(quadric, false);
        glu.gluQuadricNormals(quadric, GLU.GLU_SMOOTH);

        //head translated ontop of neck diameter
        gl.glPushMatrix();
		  gl.glTranslated(0, 0.2, 0);
		  gl.glScaled(0.6, 0.6, 0.6);
          SimpleObjects.drawFilledCylinder(gl, glu);
        gl.glPopMatrix();

        //neck
        gl.glPushMatrix();  
          gl.glTranslated(0, 0.1, 0);
          glu.gluSphere(quadric, 0.1, 10, 10);
        gl.glPopMatrix(); 
		
		//Eye
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, EYE, 0);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_EMISSION, EYE_EMISSION, 0);
		gl.glPushMatrix();
		  gl.glTranslated(0, 0.6, 0.3);
		  gl.glScaled(1, 0.5, 1);
		  doLight(gl);
		  glu.gluSphere(quadric, 0.1, 10, 10);
		gl.glPopMatrix();
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_EMISSION, DEFAULT_EMISSION, 0);

        gl.glPopMatrix();
    }
	
	public void setState(State state) {
		this.state = state;
	}
	public State getState() {
		return state;
	}
	public void setFacingRotation(double facingRotation) {
		this.facingRotation = facingRotation;
	}
	public double getFacingRotation() {
		return facingRotation;
	}
	public void setLeanRotation(double leanRotation) {
		this.leanRotation = leanRotation;
	}
	public double getLeanRotation() {
		return leanRotation;
	}
	
	public void doLight(GL2 gl) {
		eyeBeam = new Light(gl, eyeBeamIndex);
		float[] eyeBeamPosition = new float[]{0f, 0f, 0f, 1f};
		float[] eyeBeamDirection = new float[]{0f, -1.5f, 1f};
		eyeBeam.setPosition(gl, eyeBeamPosition);
		eyeBeam.setSpotlight(gl, eyeBeamDirection, 30f);
	}
	
	/**
	* State class
	* Stores all information related to the robot waiter's State at any point in time.
	*/
	public class State {
		private double time;
		private double xLocation;
		private double zLocation;
		private double forwardLean;
		
		public State(double time, double xLocation, double zLocation) {
			this.time = time;
			this.xLocation = xLocation;
			this.zLocation = zLocation;
		}
		
		public double getTime() {
			return time;
		}
		public double getXLocation() {
			return xLocation;
		}
		public double getZLocation() {
			return zLocation;
		}
	}
}
