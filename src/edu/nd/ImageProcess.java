package edu.nd;

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;


public class ImageProcess {
	public Rectangle2D findContour(BufferedImage img) {
		//Load image
		Rectangle2D ret = new Rectangle2D.Double(0,0, 1,1);
		try {
			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
			Mat targetimg = BufferedImage2Mat(img);
			Mat finalimg = Mat.zeros(targetimg.size(), CvType.CV_8UC1);
			Imgproc.threshold(targetimg, finalimg, 0, 255, Imgproc.THRESH_OTSU | Imgproc.THRESH_BINARY);
			
			List<MatOfPoint> contours = new ArrayList<>();
		    Imgproc.findContours(finalimg, contours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

		    Iterator<MatOfPoint> iterator = contours.iterator();
		    int i = 0;

		    System.out.println("Size of contour: " + contours.size());
		    double maxarea = 0;
		    int maxnum = 0;
		    while (iterator.hasNext()) {
		       MatOfPoint contour = iterator.next();
		       double area = Imgproc.contourArea(contour);
		       
		       if(maxarea < area) {
		    	   maxnum = i;
		    	   maxarea = area;
		       }
		       i++;
		    }
		    
		    MatOfPoint contour = contours.get(maxnum);
		    double epsilon = 0.1*Imgproc.arcLength(new MatOfPoint2f(contour.toArray()),true);
		    MatOfPoint2f approx = new MatOfPoint2f();
		    Imgproc.approxPolyDP(new MatOfPoint2f(contour.toArray()),approx,epsilon,true);
		    
			RotatedRect rect = Imgproc.minAreaRect(new MatOfPoint2f(contour.toArray()));
			
			ret.setRect( rect.boundingRect().x+2, 
					rect.boundingRect().y+2, 
					rect.boundingRect().width-4, 
					rect.boundingRect().height-4);
		
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return ret;
	}
	
	private Mat postImgProcess(Mat input) {

        final Size kernelSize = new Size(ImageViewer.kernel_width, ImageViewer.kernel_width);
        final Point anchor = new Point(-1, -1);
        final int iterations = ImageViewer.image_iterate;
        Mat ret = input.clone();
        
        if(ImageViewer.defaultImageProcess == EnumCollection.ImageProcess.Erosion) {
        	Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_CROSS, kernelSize);
        	Imgproc.erode(input, ret, kernel, anchor, iterations);
        } else if( ImageViewer.defaultImageProcess == EnumCollection.ImageProcess.Dialation) {
       	 	Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_CROSS, kernelSize);
       	 	Imgproc.dilate(input, ret, kernel, anchor, iterations);
        }
       		
		return ret;
	}
	
	public void doitInverse(BufferedImage img, String filename) {
		//Load image
		try {
			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
			Mat targetimg = BufferedImage2Mat(img);
			Mat finalimg = Mat.zeros(targetimg.size(), CvType.CV_8UC1);
			Mat mask = Mat.zeros(img.getHeight()+2, img.getWidth()+2, CvType.CV_8UC1);
			Imgproc.threshold(targetimg, finalimg, 0, 255, Imgproc.THRESH_OTSU | Imgproc.THRESH_BINARY_INV);
			Mat finalimg2 = finalimg.clone();			
			Imgproc.floodFill(finalimg2, mask, new Point(0,0), new Scalar(255));
			Imgproc.floodFill(finalimg2, mask, new Point(0,img.getHeight()-1), new Scalar(255));
			Imgproc.floodFill(finalimg2, mask, new Point(img.getWidth()-1,0), new Scalar(255));
			Imgproc.floodFill(finalimg2, mask, new Point(img.getWidth()-1,img.getHeight()-1), new Scalar(255));
			Imgcodecs.imwrite(filename, postImgProcess(finalimg2));
			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		
	}
	
	public void hsvfilter(BufferedImage img, String filename, int filternum) {
		//Load image
		try {
			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
			Mat targetimg = BufferedImage2MatOriginal(img);
			Mat finalimg = Mat.zeros(targetimg.size(), CvType.CV_8UC1);
			String[] lowhsv = null;
			ImageViewer.lowhsv1.split(",");
			String[] highhsv = null;
					ImageViewer.highhsv1.split(",");
			
			if(filternum == 1) {
				lowhsv = ImageViewer.lowhsv1.split(",");
				highhsv = ImageViewer.highhsv1.split(",");
			} else if (filternum ==2) {
				lowhsv = ImageViewer.lowhsv2.split(",");
				highhsv = ImageViewer.highhsv2.split(",");
			} else if (filternum ==3) {
				lowhsv = ImageViewer.lowhsv3.split(",");
				highhsv = ImageViewer.highhsv3.split(",");
			}
					
			int low_h = Integer.parseInt( lowhsv[0] );
			int low_s = Integer.parseInt( lowhsv[1] );
			int low_v = Integer.parseInt( lowhsv[2] );
			
			int high_h = Integer.parseInt( highhsv[0] );
			int high_s = Integer.parseInt( highhsv[1] );
			int high_v = Integer.parseInt( highhsv[2] );
						
			Scalar lowBound = new Scalar(low_h, low_s, low_v);
			Scalar upperBound = new Scalar(high_h, high_s, high_v);
			
			Core.inRange(targetimg, lowBound, upperBound, finalimg);
			Imgcodecs.imwrite(filename, postImgProcess(finalimg));
			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		
	}
	
	

	public void doit(BufferedImage img, String filename) {
		//Load image
		try {
			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
			Mat targetimg = BufferedImage2Mat(img);
			Mat finalimg = Mat.zeros(targetimg.size(), CvType.CV_8UC1);
			Mat mask = Mat.zeros(img.getHeight()+2, img.getWidth()+2, CvType.CV_8UC1);
			//final Mat maskCopyTo = Mat.zeros(img.getHeight()+2, img.getWidth()+2, CvType.CV_8UC1);
			Imgproc.threshold(targetimg, finalimg, 0, 255, Imgproc.THRESH_OTSU | Imgproc.THRESH_BINARY);
			Mat finalimg2 = finalimg.clone();
			if(img.getWidth() >= img.getHeight()) {
				for(int k=0; k<100; k++) {
					int newwidth = (int)((double)(img.getWidth()-1) * ((double)k/(double)100.0));
					//System.out.println("new height: " + newheight);
					Imgproc.floodFill(finalimg2, mask, new Point(newwidth, 0), new Scalar(255));
					Imgproc.floodFill(finalimg2, mask, new Point(newwidth, img.getHeight()-1), new Scalar(255));
				}
			} else {
				for(int k=0; k<100; k++) {
					int newheight = (int)((double)(img.getHeight()-1) * ((double)k/(double)100.0));
					//System.out.println("new height: " + newheight);
					Imgproc.floodFill(finalimg2, mask, new Point(0, newheight), new Scalar(255));
					Imgproc.floodFill(finalimg2, mask, new Point(img.getWidth()-1, newheight), new Scalar(255));
				}				
			}
			Imgproc.floodFill(finalimg2, mask, new Point(img.getWidth()-1,img.getHeight()-1), new Scalar(255));
			
			Imgcodecs.imwrite(filename, postImgProcess(finalimg2));
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		
	}
	
	public void doitWithoutOstu(BufferedImage img, String filename) {
		//Load image
		try {
			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
			Mat targetimg = BufferedImage2MatSpecial(img);
			Imgcodecs.imwrite(filename, targetimg);
			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		
	}

	public static Mat BufferedImage2MatOriginal(BufferedImage image) throws IOException {
	    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	    ImageIO.write(image, "png", byteArrayOutputStream);
	    byteArrayOutputStream.flush();
	    return Imgcodecs.imdecode(new MatOfByte(byteArrayOutputStream.toByteArray()), Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);
	}
	
	public static Mat BufferedImage2MatSpecial(BufferedImage image) throws IOException {
	    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	    ImageIO.write(image, "png", byteArrayOutputStream);
	    byteArrayOutputStream.flush();
	    return Imgcodecs.imdecode(new MatOfByte(byteArrayOutputStream.toByteArray()), Imgcodecs.CV_LOAD_IMAGE_COLOR);
	}
	
	public static Mat BufferedImage2Mat(BufferedImage image) throws IOException {
	    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	    ImageIO.write(image, "png", byteArrayOutputStream);
	    byteArrayOutputStream.flush();
	    return Imgcodecs.imdecode(new MatOfByte(byteArrayOutputStream.toByteArray()), Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
	}
}
