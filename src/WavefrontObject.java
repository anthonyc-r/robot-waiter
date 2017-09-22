import com.jogamp.opengl.*;
import java.io.FileInputStream;

import java.util.Arrays;
import java.util.Scanner;

//Debugging 
import java.util.logging.Logger;

public class WavefrontObject {
	/*======================LOGGER FOR DEBUGGING PURPOSES====================================*/
	public static final Logger LOGGER = Logger.getLogger(WavefrontObject.class.getName());
	
	private double[][] vertexList;
	private double[][] uvList;
	private double[][] normalList;
	private int[][][] faceList;
	
	/**
	* WFO constructor
	* Loads the data required to draw the egg from it's wavefront obj file.
	**/
	public WavefrontObject(String filename) {
		load(filename);
	}
	
	/*===================PUBLIC METHODS==================*/
	
	/** drawEgg
	* Draw the wavefront obj given the stored face-vertex data.
	* @param gl The openGL context.
	**/
	public void draw(GL2 gl) {
		double[] currentVertex;
		double[] currentNormal;
		double[] currentUV;
		
		//Begin drawing the triangles:
		gl.glBegin(GL2.GL_TRIANGLES);
			for(int[][] face : faceList){
				//vertex 0..2
				for(int i=0; i<3; i++){
					currentVertex = new double[]{vertexList[face[i][0]-1][0],
												vertexList[face[i][0]-1][1],
												vertexList[face[i][0]-1][2]};
					currentUV = new double[]{uvList[face[i][1]-1][0],
											uvList[face[i][1]-1][1]};
					currentNormal = new double[]{normalList[face[i][2]-1][0],
												normalList[face[i][2]-1][1],
												normalList[face[i][2]-1][2]};

					gl.glTexCoord2dv(currentUV, 0);
					gl.glNormal3dv(currentNormal, 0);
					gl.glVertex3dv(currentVertex, 0);
				}
			}
		gl.glEnd();
	}
	
	/*====================PRIVATE METHODS==================*/
	
	/**
	* loadFile
	* Load the vertex and face list from the file.
	* @param filename egg object file name.
	**/
	private void load(String filename) {
		
		int verts = 0;
		int uvs = 0;
		int norms = 0;
		int faces = 0;
		
		//Temp storage
		String[] line;
		String identifier;
		String[] vert1;
		String[] vert2;
		String[] vert3;
		String[] vert4;
		String[][] sFace = new String[4][3];
		int[][] face = new int[4][3];
		
		
		//useful indexes
		int currentVert = 0;
		int currentUv = 0;
		int currentNorm = 0;
		int currentFace = 0;
		
		//DEBUGGING
		LOGGER.info("Egg.loadFile Entered.");
		//DEBUGGING END
		
		//IO setup
		FileInputStream in = null;
		Scanner scanner = null;
		
		//Set up the file input must throw exception
		try {
			in = new FileInputStream(filename);
			scanner = new Scanner(in);
		}
		catch(Exception e) {
			LOGGER.severe("FILE NOT FOUND!");
			e.printStackTrace();
		}
		
		//DEBUG
		LOGGER.info("Found wavefront object file!");
		//DEBUG END
		
		//Quick scan to count the number of faces and vertices.
		while(scanner.hasNextLine()) {
			//more useful split by spaces
			line = scanner.nextLine().split(" ");
			//the identifier is the first part
			identifier = line[0];
			
			//DEBUG
			//LOGGER.info("Line identifier:"+identifier);
			//DEBUG END
			
			//count verts and faces.
			if(identifier.equals("v")) {
				verts++;
			}
			else if(identifier.equals("vt")){
				uvs++;
			}
			else if(identifier.equals("vn")){
				norms++;
			}
			else if(identifier.equals("f")){
				faces++;
			}
		}
		
		//Set up storage arrays.
		vertexList = new double[verts][3];
		uvList = new double[uvs][2];
		normalList = new double[norms][3];
		//faces num of 3x3 values, v1i,v1uvi,v1ni,...,v3i,v3uvi,v3ni
		faceList = new int[faces][3][3]; //faces*sizeof(int) of wasted space

		//scanner.reset();
		//Scan through again for vertices and normals, calc uv while im at it.
		scanner.close();
		try {
			in = new FileInputStream(filename);
			scanner = new Scanner(in);
		}
		catch(Exception e) {
			LOGGER.severe("FILE NOT FOUND!");
			e.printStackTrace();
		}
		
		while(scanner.hasNextLine()) {
			line = scanner.nextLine().split(" ");
			//the identifier is the first section split by a space.
			identifier = line[0];
			//DEBUG
			//LOGGER.info("Line identifier:"+identifier);
			//DEBUG END
			
			//vertices are listed first
			if(identifier.equals("v")) {
				vertexList[currentVert] = new double[]{Double.valueOf(line[1]),
													Double.valueOf(line[2]),
													Double.valueOf(line[3])};
				currentVert++;
			}
			//then uv coords
			else if(identifier.equals("vt")){
				uvList[currentUv] = new double[]{Double.valueOf(line[1]),
												Double.valueOf(line[2])};
				currentUv++;
			}
			//then normals...
			else if(identifier.equals("vn")){
				normalList[currentNorm] = new double[]{Double.valueOf(line[1]), 
													Double.valueOf(line[2]), 
													Double.valueOf(line[3])};
				currentNorm++;
			}
			//finally read face indexes
			else if(identifier.equals("f")){
				vert1 = line[1].split("/");
				vert2 = line[2].split("/");
				vert3 = line[3].split("/");
				sFace = new String[][]{vert1, vert2, vert3};
				
				//mass conversion
				for(int i=0; i<3; i++){
					for(int j=0; j<3; j++){
						face[i][j] = Integer.valueOf(sFace[i][j]);
					}
				}
				
				//store the 3 vertex,uv,normal indexes.
				faceList[currentFace] = new int[][]{{face[0][0], face[0][1], face[0][2]}, //Vertex1, vert, uv, norm
													{face[1][0], face[1][1], face[1][2]}, //Vertex2, vert, uv, norm
													{face[2][0], face[2][1], face[2][2]}};//vertex3, vert, uv, norm
				currentFace++;
			}
		}
		
		LOGGER.info("Verts:"+verts+" UVs: "+uvs+" Faces:"+faces+" Norms:"+norms);
		
	}
	
}

