import com.jogamp.opengl.*; 
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.gl2.GLUT;

import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;


public class AnimatedRobotWaiter extends RobotWaiter {
	//State class defined at bottom.
	private ArrayList<State> keyframes;
	
	public static final int FRAMES_PER_SECOND = 30;
	
	/**
	* RobotWaiter
	* Draws an animated robot waiter.
	* @param fileName The file name of the xml document containing a number of keyframes.
	**/
    public AnimatedRobotWaiter(String fileName, int eyeBeamIndex) {
		super(5, 0, 5, eyeBeamIndex);
		keyframes = new ArrayList<State>();
		//Load keyframes
		loadKeyframes(fileName);
    }
	
	/**
	* getCurrentState
	* Returns the current frame, linearly interpolated based on the robot's
	* array of keyframes.
	* @param time The current time, animation occurs for 1 minute, thus time ranges from
	*   0-60*FRAMES_PER_SECOND;
	**/
	public void updateState(double time) {
		State prevKeyframe = null;
		State nextKeyframe = null;
		double timeDifference;
		double timeProgression;
		double pcProgression;
		double xLocation;
		double zLocation;
		
		for(State keyframe : keyframes){
			//Iterate until a Keyframe with a time above the current time is found
			if(keyframe.getTime() > time){
				//Set the Keyframes required to interpolate between
				nextKeyframe = keyframe;
				break;
			}
			//Else it is before and will be the previous keyframe
			else{
				prevKeyframe = keyframe;
			}
		}
		
		//If the end is reached with no next keyframe, stay still.
		if(nextKeyframe == null){
			nextKeyframe = prevKeyframe;
		}
		
		//Linearly interpolate between previous and next keyframes.
		timeDifference = nextKeyframe.getTime() - prevKeyframe.getTime();
		timeProgression = time - prevKeyframe.getTime();
		pcProgression = timeProgression/timeDifference;
		
		xLocation = prevKeyframe.getXLocation()+pcProgression*(nextKeyframe.getXLocation() - prevKeyframe.getXLocation());
		zLocation = prevKeyframe.getZLocation()+pcProgression*(nextKeyframe.getZLocation() - prevKeyframe.getZLocation());		
		/*
		System.out.println("PREV TIME:"+prevKeyframe.getTime()+" X:"+prevKeyframe.getXLocation()+" Z:"+prevKeyframe.getZLocation());
		System.out.println("NEXT TIME: "+nextKeyframe.getTime()+" X:"+nextKeyframe.getXLocation()+" Z:"+nextKeyframe.getZLocation());
		System.out.println("x:"+xLocation+" z:"+zLocation);
		*/
		State newState = new State(time, xLocation, zLocation);
		double facingRotation = getFacingRotation(prevKeyframe, nextKeyframe);
		double leanRotation = getLeanRotation(prevKeyframe, nextKeyframe);
		setFacingRotation(facingRotation);
		setLeanRotation(leanRotation);
		setState(newState);
	}
	
	/**
	* getCurrentState
	* Returns the current frame, interpolated  using a bezier curve based on the robot's
	* array of keyframes.
	* @param time The current time, animation occurs for 1 minute, thus time ranges from
	*   0-60*FRAMES_PER_SECOND;
	**/
	public void updateStateCurve(double time) {
		int bezStartIndex = 0;
		int prevKeyIndex = 0;
		int endKeyIndex = 0;
		State prevKeyframe;
		State curveStartFrame;
		State curveEndFrame;
		int numKeyframes = keyframes.size();
		double timeProgression;
		double timeDifference;
		double u = 0;
		double[] xs = new double[4];
		double[] ys = new double[4];
		double[] ts = new double[4];
		
		double x;
		double y;
		
		for(int i=0; i<numKeyframes; i++){
			//Iterate until a Keyframe with a time above the current time is found
			if(keyframes.get(i).getTime() > time){
				//Break if found a frame after current tie
				break;
			}
			//Else it is before and will be the previous keyframe
			else{
				prevKeyIndex = i;
			}
		}

		//Set the index at the start of the 4 points needing to be got
		bezStartIndex = (prevKeyIndex/3)*3;
		
		curveStartFrame = keyframes.get(bezStartIndex);
		curveEndFrame = keyframes.get(bezStartIndex+3);
		
		//Linearly interpolate local time
		timeDifference = curveEndFrame.getTime() - curveStartFrame.getTime();
		timeProgression = time - curveStartFrame.getTime();
		//percent through current curve
		u = timeProgression/timeDifference;
		
		//Fill arrays
		for(int i=0; i<4; i++){
			xs[i] = keyframes.get(bezStartIndex+i).getXLocation();
			ys[i] = keyframes.get(bezStartIndex+i).getZLocation();
		}
	
		
		x = xs[0]*Math.pow((1-u),3)+
			xs[1]*3*u*Math.pow((1-u), 2)+
			xs[2]*3*Math.pow(u, 2)*(1-u)+
			xs[3]*Math.pow(u, 3);
		y = ys[0]*Math.pow((1-u),3)+
			ys[1]*3*u*Math.pow((1-u), 2)+
			ys[2]*3*Math.pow(u, 2)*(1-u)+
			ys[3]*Math.pow(u, 3);
		

		State newState = new State(time, x, y);
		double facingRotation = getFacingRotation(getState(), newState);
		double leanRotation = getLeanRotation(getState(), newState);
		setFacingRotation(facingRotation);
		setLeanRotation(leanRotation);
		setState(newState);
	}
	
	private double getFacingRotation(State state1, State state2) {
		double lastX = state1.getXLocation();
		double lastZ = state1.getZLocation();
		double x = state2.getXLocation();
		double z = state2.getZLocation();
		//calculate angle 
		double theta = (Math.atan2(x-lastX, lastZ-z)+(Math.PI*2))%(Math.PI*2);
		
		return Math.toDegrees(theta);
	}

	
	private double getLeanRotation(State state1, State state2) {
		double k = 45;
		double lastX = state1.getXLocation();
		double lastZ = state1.getZLocation();
		double x = state2.getXLocation();
		double z = state2.getZLocation();
		
		double dx = x-lastX;
		double dz = z-lastZ;
		//lean amount is some k*length of velocity
		double length = k*Math.sqrt(dx*dx+dz*dz);
		
		//limit lean amount to 30deg
		double lean = Math.min(length, 30);

		return lean;
	}
	
	private void loadKeyframes(String fileName) {
		double time;
		double xLocation;
		double yLocation;

		try {
			File keyframesXml = new File(fileName);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(keyframesXml);
			
			NodeList nList = doc.getElementsByTagName("keyframe");
			
			for(int i=0; i<nList.getLength(); i++){
				Node keyframeNode = nList.item(i);
				Element elem = (Element) keyframeNode;
				String sTime = elem.getElementsByTagName("time").item(0).getTextContent();
				String sXLocation = elem.getElementsByTagName("xLocation").item(0).getTextContent();
				String sYLocation = elem.getElementsByTagName("yLocation").item(0).getTextContent();
				
				time = Double.valueOf(sTime);
				xLocation = Double.valueOf(sXLocation);
				yLocation = Double.valueOf(sYLocation);
				
				State keyframe = new State(time, xLocation, yLocation);
				keyframes.add(keyframe);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
