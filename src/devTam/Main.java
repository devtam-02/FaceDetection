package devTam;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.opencv.videoio.VideoCapture;

public class Main extends JFrame{
	
	//Camera screen
	private JLabel cameraScreen;
	private JButton btnCapture;
	private VideoCapture capture;
	private Mat image;
	private boolean clicked = false;
	private MatOfRect faces;
	
	public Main() {
		
		setLayout(null);
		
		cameraScreen = new JLabel();
		cameraScreen.setBounds(0, 0, 640, 480);
		add(cameraScreen);
		
		btnCapture = new JButton("Capture");
		btnCapture.setBounds(300, 480, 80, 40);
		add(btnCapture);
		
		btnCapture.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				clicked = true;
				
			}
		});
		
		setSize(new Dimension(640, 560));
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	public void startCamera() {
		capture = new VideoCapture(0);
		image = new Mat();
		faces = new MatOfRect();
		byte[] imageData;
		ImageIcon icon;
		while(true) {
			//read image to matrix
			capture.read(image);
			Core.flip(image, image, 1);
			
			//detect faces
			CascadeClassifier faceCascade = new CascadeClassifier();
			
			//load trained data file
			faceCascade.load("data/haarcascade_frontalface_alt2.xml");
			
			
			int height = image.height();
			int absoluteFaceSize = 0;
			if(Math.round(height * 0.2f) > 0)
				absoluteFaceSize = Math.round(height * 0.2f);
			//d
			faceCascade.detectMultiScale(image, faces, 1.1, 2, 0|Objdetect.CASCADE_SCALE_IMAGE,
					new Size(absoluteFaceSize, absoluteFaceSize));
			Rect[] faceArray = faces.toArray();
//			image = faceArray[0];
			
			for (int i = 0; i < faceArray.length; i++) {
				Imgproc.rectangle(image, faceArray[i], new Scalar(0,0,255), 3);
			}
			
			//convert matrix to byte
			final MatOfByte buf = new MatOfByte();
			Imgcodecs.imencode(".jpg", image, buf);
			imageData = buf.toArray();
			//add to JLabel
			icon = new ImageIcon(imageData);
			cameraScreen.setIcon(icon);
			//capture and save to file
			if(clicked) {
				//prompt for enter image name
				String name = JOptionPane.showInputDialog(this, "Enter image name: ");
				if(name.equals("")) {
					name = new SimpleDateFormat("hh-mm-ss__yyyy-mm-dd").format(new Date());
					
				}
				//write to file				
				Imgcodecs.imwrite("images/" + name + ".jpg", image);				
				clicked = false;
			}
		}
	}
	public static void main(String[] args){
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		System.out.println("load successfully");
		EventQueue.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				Main m = new Main();
				//start camera in thread
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						m.startCamera();
					}
				}).start();;
				
			}
		});
	}
}
