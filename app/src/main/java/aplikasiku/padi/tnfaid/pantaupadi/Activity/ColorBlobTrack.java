package aplikasiku.padi.tnfaid.pantaupadi.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import aplikasiku.padi.tnfaid.pantaupadi.R;

public class ColorBlobTrack {

//    lower dan upper bound untuk check range pada color space HSV
    private Scalar mLowerBound = new Scalar(0);
    private Scalar mUpperbound = new Scalar(0);

//    minimun contour area dalam satusan percent untuk filtering countour
    private static double mMinContourArea = 0.1;

//    Color radius untuk checking range pada color space HSV
    private Scalar mColorRadius = new Scalar(10, 20, 20, 0);
    private Mat mSpectrum = new Mat();
    private List<MatOfPoint> mContours = new ArrayList<MatOfPoint>();

//    Cache
    Mat mPyrDownMat = new Mat();
    Mat mHsvMat = new Mat();
    Mat mMask = new Mat();
    Mat mDilateMask = new Mat();
    Mat mHierarchy = new Mat();

    public void setmColorRadius(Scalar radius)
    {
        mColorRadius = radius;
    }

//    ini untuk konversi warna dari hsv ke RGB biar bisa di proses
    public void setHsvColor(Scalar hsvColor)
    {
        double minH = (hsvColor.val[0] >= mColorRadius.val[0] ? hsvColor.val[0]-mColorRadius.val[0]: 0);
        double maxH = (hsvColor.val[0] + mColorRadius.val[0] <= 255 ? hsvColor.val[0]+mColorRadius.val[0] : 255);

        mLowerBound.val[0] = minH;
        mUpperbound.val[0] = maxH;

        mLowerBound.val[1] = hsvColor.val[1] - mColorRadius.val[1];
        mUpperbound.val[1] = hsvColor.val[1] + mColorRadius.val[1];

        mLowerBound.val[2] = hsvColor.val[2] - mColorRadius.val[2];
        mUpperbound.val[2] = hsvColor.val[2] + mColorRadius.val[2];

        mLowerBound.val[3] = 0;
        mUpperbound.val[3] = 255;

        Mat spectrumHsv = new Mat(1, (int)(maxH-minH), CvType.CV_8UC3);

        for (int j =0; j < maxH - minH; j++)
        {
            byte[] tmp = {(byte)(minH+j), (byte)255, (byte)255};
            spectrumHsv.put(0, j, tmp);
        }
        Imgproc.cvtColor(spectrumHsv, mSpectrum, Imgproc.COLOR_HSV2RGB_FULL, 4);
    }

    public Mat getmSpectrum()
    {
        return mSpectrum;
    }

    public void setmMinContourArea(double area){
        mMinContourArea = area;
    }

//    proses itu si rgb
    public void process(Mat rgbaImage)
    {
        Imgproc.pyrDown(rgbaImage, mPyrDownMat);
        Imgproc.pyrDown(mPyrDownMat, mPyrDownMat);

        Imgproc.cvtColor(mPyrDownMat, mHsvMat, Imgproc.COLOR_RGB2HSV);

        Core.inRange(mHsvMat, mLowerBound, mUpperbound, mMask);
        Imgproc.dilate(mMask, mDilateMask, new Mat());

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

        Imgproc.findContours(mDilateMask, contours, mHierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

//        untuk menemukan max countour area
        double maxArea = 0;
        Iterator<MatOfPoint> each = contours.iterator();
        while (((Iterator) each).hasNext()){
            MatOfPoint wrapper = each.next();
            double area = Imgproc.contourArea(wrapper);
            if (area > maxArea)
                maxArea = area;
        }

//        Filter contour berdasarkan area dan resize biar pas pada original image size
        mContours.clear();
        each = contours.iterator();
        while (each.hasNext()) {
            MatOfPoint contour = each.next();
            if (Imgproc.contourArea(contour) > mMinContourArea*maxArea)
            {
                Core.multiply(contour, new Scalar(4,4), contour);
                mContours.add(contour);
            }
        }
    }
    public List<MatOfPoint> getContours()
    {
        return mContours;
    }
}
