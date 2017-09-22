import com.jogamp.opengl.*;
import com.jogamp.opengl.util.texture.*;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;

/**
* A cylinder with both ends filled in
* The base aligned with the x, z plane, centered at x,y = 0.
* Extends upward by Height in the positive y direction.
* Diameter = 1, Height = 1.
**/
public class SimpleObjects {
    
    public static void drawFilledCylinder(GL2 gl, GLU glu) {
        gl.glPushMatrix();
          GLUquadric quadric = glu.gluNewQuadric();
          glu.gluQuadricDrawStyle(quadric, GLU.GLU_FILL);
          //No texture currently.
          glu.gluQuadricTexture(quadric, false);
          //smooth poly normals?
          glu.gluQuadricNormals(quadric, GLU.GLU_SMOOTH);

          gl.glRotated(90, -1, 0, 0);
          glu.gluCylinder(quadric, 0.5, 0.5, 1, 20, 40);
        gl.glPopMatrix();

        //need to add disks to the top and bottom
        //bottom disk
        gl.glPushMatrix();
          gl.glRotated(90, 1, 0, 0);
          glu.gluDisk(quadric, 0, 0.5, 20, 20);
        gl.glPopMatrix();
        //top disk
        gl.glPushMatrix();
          gl.glTranslated(0, 1, 0);
          gl.glRotated(90, -1, 0, 0);
          glu.gluDisk(quadric, 0, 0.5, 20, 20);
        gl.glPopMatrix();
    }


    /**
    * drawTexturedCube
    * @param gl The gl context.
    * @param texs The textures to use drawing this.
    *   The first element is  the top(or bottom if room)
    *   texture, second is the bottom(or top if room) face.
    *   Then the next 4 are textures for the faces starting at the
    *   negative x face, going around clockwise.
    * @param room Whether this is a room or a cube, determines
    *   if faces face inwards or outwards.
    **/
    public static void drawTexturedCube(GL2 gl, Texture[] texs,
            boolean room) {

        //top or bottom if room
        gl.glPushMatrix();
          if(!room){
              gl.glTranslated(0, 1, 0);
          }
          drawTexturedSquare(gl, texs[0]);
        gl.glPopMatrix();
        //bottom
        gl.glPushMatrix();
          if(room){
              gl.glTranslated(0, 1, 0);
          }
          gl.glTranslated(1, 0, 0);
          gl.glRotated(180, 0, 0, 1);
          drawTexturedSquare(gl, texs[1]);
        gl.glPopMatrix();

        //walls
        gl.glPushMatrix();
        //Move back into alignment
        gl.glTranslated(0.5, 0, 0.5);
        for(int i=0; i<4; i+=1){
            gl.glRotated(90*i, 0, 1, 0); 
            //Align ready from rotation around y
            gl.glPushMatrix();
              //Make walls easier
              if(!room){
                  gl.glTranslated(-0.5, 0, -0.5);
              }
              else{
                  gl.glTranslated(0.5, 0, -0.5);
              }
              //Align it vertically
              gl.glRotated(90, 0, 0, 1);
              drawTexturedSquare(gl, texs[i+2]);
            gl.glPopMatrix();
        }
        gl.glPopMatrix();
    }

    public static void drawCube(GL2 gl, boolean room) {
        //top or bottom if room
        gl.glPushMatrix();
          if(!room){
              gl.glTranslated(0, 1, 0);
          }
          drawSquare(gl);
        gl.glPopMatrix();
        //bottom
        gl.glPushMatrix();
          if(room){
              gl.glTranslated(0, 1, 0);
          }
          gl.glTranslated(1, 0, 0);
          gl.glRotated(180, 0, 0, 1);
          drawSquare(gl);
        gl.glPopMatrix();

        //walls
        gl.glPushMatrix();
        //Move back into alignment
        gl.glTranslated(0.5, 0, 0.5);
        for(int i=0; i<4; i+=1){
            gl.glRotated(90*i, 0, 1, 0); 
            //Align ready from rotation around y
            gl.glPushMatrix();
              //Make walls easier
              if(!room){
                  gl.glTranslated(-0.5, 0, -0.5);
              }
              else{
                  gl.glTranslated(0.5, 0, -0.5);
              }
              //Align it vertically
              gl.glRotated(90, 0, 0, 1);
              drawSquare(gl);
            gl.glPopMatrix();
        }
        gl.glPopMatrix();
    }

    /**
    * drawTexturedSquare
    **/
    public static void drawTexturedSquare(GL2 gl, Texture tex) {
        tex.enable(gl);
        tex.bind(gl);
        tex.setTexParameteri(gl, GL2.GL_TEXTURE_MAG_FILTER,
                    GL2.GL_LINEAR);
        tex.setTexParameteri(gl, GL2.GL_TEXTURE_MIN_FILTER,
                    GL2.GL_LINEAR);
        tex.setTexParameteri(gl, GL2.GL_TEXTURE_ENV_MODE, 
                GL2.GL_MODULATE);
        drawSquare(gl);
        tex.disable(gl);
    }

    /**      
    * Draw a square on the x,z plane with verticies 0,0,0 and
    * 1,0,1
    **/
    public static void drawSquare(GL2 gl) {
        gl.glPushMatrix();
          gl.glBegin(GL2.GL_TRIANGLE_STRIP);
            gl.glTexCoord2d(1, 0);
            gl.glVertex3d(0, 0, 0);
			
            gl.glTexCoord2d(0, 0);
            gl.glVertex3d(0, 0, 1);
			
            gl.glTexCoord2d(1, 1);
            gl.glVertex3d(1, 0, 0);
			
            gl.glTexCoord2d(0, 1);
            gl.glVertex3d(1, 0, 1);
          gl.glEnd();
        gl.glPopMatrix();
        
    }

    public static void drawVertSquare(GL2 gl, Texture tex) {
        tex.enable(gl);
        tex.bind(gl);
        tex.setTexParameteri(gl, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);
		
		gl.glPushMatrix();
          gl.glBegin(GL2.GL_TRIANGLE_STRIP);
            gl.glTexCoord2d(0, 0);
            gl.glVertex3d(0, 0, 0);
            gl.glTexCoord2d(1, 0);
            gl.glVertex3d(1, 0, 0);
            gl.glTexCoord2d(0, 1);
            gl.glVertex3d(0, 1, 0);
            gl.glTexCoord2d(1, 1);
            gl.glVertex3d(1, 1, 0);
          gl.glEnd();
        gl.glPopMatrix();
        tex.disable(gl);
    }

    
}
