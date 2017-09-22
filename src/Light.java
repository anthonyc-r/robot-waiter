import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.texture.*;
import com.jogamp.opengl.util.texture.awt.*;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.awt.GLCanvas;

public class Light {
	
	private int index;
	private boolean spotlight;
	private float[] ambient;
	private float[] diffuse;
	private float[] specular;

	
	/**
	* constructor
	**/
	public Light(GL2 gl, int index) {
		this.index = index;
		gl.glEnable(index);
		ambient = new float[]{0.5f, 0.5f, 0.5f};
		diffuse = new float[]{0.5f, 0.5f, 0.5f};
		specular = new float[]{0.5f, 0.5f, 0.5f};
		
		gl.glLightfv(index, GL2.GL_AMBIENT, ambient, 0);
		gl.glLightfv(index, GL2.GL_DIFFUSE, diffuse, 0);
		gl.glLightfv(index, GL2.GL_SPECULAR, specular, 0);
		
	}
	
	/**
	* setPosition
	* Specify the position of the light
	**/
	public void setPosition(GL2 gl, float[] position) {
		gl.glLightfv(index, GL2.GL_POSITION, position, 0);
	}
	
	/**
	* setDirection
	* Set the direction value of the light
	**/
	public void setDirection(GL2 gl, float[] direction) {
		gl.glLightfv(index, GL2.GL_SPOT_DIRECTION, direction, 0);
	}
	
	/**
	* makeSpotLight
	* Makes the light a spotlight with the given direction and cutoff angle
	**/
	public void setSpotlight(GL2 gl, float[] direction, float angle) {
        gl.glLightf(index, GL2.GL_SPOT_CUTOFF, angle);
        gl.glLightfv(index, GL2.GL_SPOT_DIRECTION, direction, 0);
		gl.glLightf(index, GL2.GL_SPOT_EXPONENT, 2.0f);
	}
	
	/**
	* disable
	* disables this light source
	**/
	public void disable(GL2 gl) {
		gl.glDisable(index);
	}
	
	/**
	* enable
	* enables this light source
	**/
	public void enable(GL2 gl) {
		gl.glEnable(index);
	}
	
	
}