import com.jogamp.opengl.*;
import com.jogamp.opengl.glu.GLU;

public class Table {
	private double radius;
	
    public Table(double radius) {
        this.radius = radius;   
    }

    public void draw(GL2 gl, GLU glu) {
        //Circular table..
        gl.glPushMatrix();

        gl.glPushMatrix();
          gl.glTranslated(0, 2, 0);
          gl.glScaled(radius, 0.1, radius);  
          SimpleObjects.drawFilledCylinder(gl, glu);
        gl.glPopMatrix();

        gl.glPushMatrix();
          gl.glScaled(0.3, 2, 0.3);
          gl.glTranslated(-0.5, 0, -0.5);
          SimpleObjects.drawCube(gl, false);
        gl.glPopMatrix();

        gl.glPopMatrix();
    }
}
