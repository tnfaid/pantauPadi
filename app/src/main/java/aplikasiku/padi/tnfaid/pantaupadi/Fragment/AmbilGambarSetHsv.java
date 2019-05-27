package aplikasiku.padi.tnfaid.pantaupadi.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import aplikasiku.padi.tnfaid.pantaupadi.Activity.ColorBlobDetector;
import aplikasiku.padi.tnfaid.pantaupadi.R;


public class AmbilGambarSetHsv extends Fragment implements View.OnTouchListener, CameraBridgeViewBase.CvCameraViewListener2 {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = "OCVSample::Activity";

    // TODO: Rename and change types of parameters
    private boolean         mIsColorSelected = false;
    private Mat             mRgba;
    private Mat             mSpectrum;

    private Scalar          mBlobColorRgba;
    private Scalar          mBlobColorHsv;
    private Scalar          colorRadius;

    private ColorBlobDetector   mDetector;
    private Size            SPECTRUM_SIZE;
    private Scalar          CONTOUR_COLOR;
    private int             maxVals=10000;
    private CameraBridgeViewBase    mOpenCvCameraView;
    private boolean         isTracking = false;
    private Button          startTracking;
    private Button          stopTracking;
    private SeekBar         seekBarH, seekBarS, seekBarV;
    private int             Hval, Sval, Vval;
    private EditText        massEditText;
    private TextView        trackStatus;
    private double          mass;
    private ArrayList       locations = new ArrayList();
    private ArrayList       timeStamps = new ArrayList();

//    ini nanti akan berisi kecepatan secara instan untuk waktu antara 0.1 hinggga 0.9
    private ArrayList       instantVelocities = new ArrayList();
    private TextView        avgVel;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this.getActivity()) {
        @Override
        public void onManagerConnected(int status) {
            switch (status)
            {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "Open CV loaded succesfully");
                    mOpenCvCameraView.enableView();
                    mOpenCvCameraView.setOnTouchListener(AmbilGambarSetHsv.this);
                } break;
                default :
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public void makeInstantVelocitiesList()
    {
//        catatan: ketika menggunakakan index i-1 dan i+1 untuk menghitung kecepatan dari index i
//        ini berarti kita tidak dapat menghitung secara kecepatan secara instan ketika i =0 dan i =posisition.size()
//        Bagaimanapun jika dimulai dari 0, kamu dapat meletakkan 0 pada permulaan daftar 'instanVelocities

        for (int i = 1; i < locations.size()-1; i= i+1)
        {
            double p1 = (double)locations.get(i - 1);
            double p2 = (double)locations.get(i + 1);
            double t1 = (double)timeStamps.get(i - 1);
            double t2 = (double)timeStamps.get(i + 1);

            double instanVelocity = (p2 - p1) / (t2 - t1);
            instantVelocities.add(instanVelocity);
        }
    }

    public double getChangeInVelocity()
    {
        double initialX = (double)locations.get(0);
        double finalX = (double)locations.get(locations.size()-1);
        long initialTime = (long)timeStamps.get(0); //ini harus 0
        long finalTime = (long)timeStamps.get(timeStamps.size()-1);

        long changeInTimeLong = (finalTime - initialTime);
        double changeInTime = (double)changeInTimeLong/Math.pow(10,9);
        double changeInPos = finalX - initialX;

        return changeInPos/changeInTime;
    }

    public double getChangeInKinetic()
    {
//      rumus energi kinetik = (1/2)mv^2
        double initialVelocity = (double)instantVelocities.get(0);//get the first
        double finalVelocity = (double)instantVelocities.get(instantVelocities.size()-1);//get the last
        double initialKinetic = (1/2)*mass*(Math.pow(initialVelocity, 2));//math.pow(base, exponent
        double finalKinetic = (1/2)*mass*(Math.pow(finalVelocity, 2));
        double changeKinetic = finalKinetic - initialKinetic;
        return changeKinetic;
    }

    public double getPower(double changeInKineticPassed, double changeInTimePassed){
        double power = changeInKineticPassed/changeInTimePassed;
        return power;
    }


    public AmbilGambarSetHsv() {
        Log.i(TAG, "Instantiated new" + this.getClass());
    }


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
//        getActivity().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        getActivity().setContentView(R.layout.fragment_ambil_gambar_set_hsv);
        trackStatus = (TextView) getActivity().findViewById(R.id.trackStatus);
        startTracking = (Button)getActivity().findViewById(R.id.trackButtonUI);
        stopTracking = (Button)getActivity().findViewById(R.id.stopButtonUI);
        startTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isTracking = true;
                Log.e(TAG,"Tracking turned ON!");
                trackStatus.setText("Tracking ON!");
            }
        });

        stopTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isTracking = false;
                Log.e(TAG,"Tracking turned OFF!");
                trackStatus.setText("Tracking OFF! Avg Speed = " + String.format("%3.2f",getChangeInVelocity()));

                //double deltaX = Math.abs((double)locations.get(locations.size()-1)-(double)locations.get(0)) ;
                //Toast.makeText(getApplicationContext(),"Delta Time: "+deltaTime,Toast.LENGTH_SHORT).show();
                //Toast.makeText(getApplicationContext(),"Delta X: "+deltaX,Toast.LENGTH_SHORT).show();



            }
        });
        massEditText = (EditText)getActivity(). findViewById(R.id.editTextMass);
        massEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length()!=0) {
                    mass = Double.parseDouble(massEditText.getText().toString());
                    // Toast.makeText(getApplicationContext(), "Mass is " + mass, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });

        avgVel = (TextView)getActivity().findViewById(R.id.tvAvgVelUI);

        seekBarH = (SeekBar)getActivity().findViewById(R.id.seekBarH);
        seekBarS = (SeekBar)getActivity().findViewById(R.id.seekBarS);
        seekBarV = (SeekBar)getActivity().findViewById(R.id.seekBarV);


        seekBarH.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int currentProg;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentProg = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Hval = currentProg;
                Toast.makeText(getActivity().getApplicationContext(),"Final H Val " + Hval,Toast.LENGTH_SHORT).show();

            }
        });

        seekBarS.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int currentProg;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentProg = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Sval = currentProg;
                Toast.makeText(getActivity().getApplicationContext(),"Final S Val " + Sval,Toast.LENGTH_SHORT).show();

            }
        });

        seekBarV.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int currentProg;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentProg = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Vval = currentProg;

                Toast.makeText(getActivity().getApplicationContext(),"Final V Val " + Vval,Toast.LENGTH_SHORT).show();

            }
        });
        mOpenCvCameraView = (CameraBridgeViewBase)getActivity().findViewById(R.id.color_blob_detection_activity_surface_view);
        mOpenCvCameraView.setCvCameraViewListener(this);
        if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)) {
            Log.d(TAG, "Everything should be fine with using the camera.");
        } else {
            Log.d(TAG, "Requesting permission to use the camera.");
            String[] CAMERA_PERMISSIONS = {
                    Manifest.permission.CAMERA
            };
            ActivityCompat.requestPermissions(getActivity(), CAMERA_PERMISSIONS, 0);
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, getActivity() , mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mDetector = new ColorBlobDetector();
        mSpectrum = new Mat();
        mBlobColorRgba = new Scalar(255);
        mBlobColorHsv = new Scalar(255);
        SPECTRUM_SIZE = new Size(100, 32);
        CONTOUR_COLOR = new Scalar(255,0,0,255);
    }

    public void onCameraViewStopped() {
        mRgba.release();
    }

    public boolean onTouch(View v, MotionEvent event) {
        int cols = mRgba.cols();
        int rows = mRgba.rows();

        int xOffset = (mOpenCvCameraView.getWidth() - cols) / 2;
        int yOffset = (mOpenCvCameraView.getHeight() - rows) / 2;

        int x = (int)event.getX() - xOffset;
        int y = (int)event.getY() - yOffset;

        Log.i(TAG, "Touch image coordinates: (" + x + ", " + y + ")");

        if ((x < 0) || (y < 0) || (x > cols) || (y > rows)) return false;

        Rect touchedRect = new Rect();

        touchedRect.x = (x>4) ? x-4 : 0;
        touchedRect.y = (y>4) ? y-4 : 0;

        touchedRect.width = (x+4 < cols) ? x + 4 - touchedRect.x : cols - touchedRect.x;
        touchedRect.height = (y+4 < rows) ? y + 4 - touchedRect.y : rows - touchedRect.y;

        Mat touchedRegionRgba = mRgba.submat(touchedRect);

        Mat touchedRegionHsv = new Mat();
        Imgproc.cvtColor(touchedRegionRgba, touchedRegionHsv, Imgproc.COLOR_RGB2HSV_FULL);

        // Calculate average color of touched region
        mBlobColorHsv = Core.sumElems(touchedRegionHsv);
        int pointCount = touchedRect.width*touchedRect.height;
        for (int i = 0; i < mBlobColorHsv.val.length; i++)
            mBlobColorHsv.val[i] /= pointCount;
        Toast.makeText(getActivity(),"RGB = " + mBlobColorRgba,Toast.LENGTH_LONG).show();
        mBlobColorRgba = converScalarHsv2Rgba(mBlobColorHsv);

        Log.i(TAG, "Touched rgba color: (" + mBlobColorRgba.val[0] + ", " + mBlobColorRgba.val[1] +
                ", " + mBlobColorRgba.val[2] + ", " + mBlobColorRgba.val[3] + ")");

        mDetector.setHsvColor(mBlobColorHsv);


        //Imgproc.resize(mDetector.getSpectrum(), mSpectrum, SPECTRUM_SIZE);

        mIsColorSelected = true;


        touchedRegionRgba.release();
        touchedRegionHsv.release();

        return false; // don't need subsequent touch events
    }

    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        colorRadius = new Scalar(Hval,Sval,Vval,0);

        if (mIsColorSelected) {
            mDetector.setColorRadius(colorRadius);
            mDetector.process(mRgba);

            List<MatOfPoint> contours = mDetector.getContours();

            // MatOfPoint cnt;
            //Log.e(TAG, "Contours count: " + contours.size());
            Imgproc.drawContours(mRgba, contours, -1, CONTOUR_COLOR,3);
            MatOfPoint2f approxCurve = new MatOfPoint2f();

            /*if(contours.size() == 1) {
                if (isStart)
                    isTracking = true;
                else
                    isTracking = false;
            }
            else {
                isTracking = false;
            }*/

            if(contours.size() == 1) {

                //Convert contours(i) from MatOfPoint to MatOfPoint2f
                MatOfPoint2f contour2f = new MatOfPoint2f(contours.get(0).toArray());
                //Processing on mMOP2f1 which is in type MatOfPoint2f
                double approxDistance = Imgproc.arcLength(contour2f, true) * 0.02;
                Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);

                //Convert back to MatOfPoint
                MatOfPoint points = new MatOfPoint(approxCurve.toArray());

                // Get bounding rect of contour
                Rect rect = Imgproc.boundingRect(points);

                // draw enclosing rectangle (all same color, but you could use variable i to make them unique)
                Point center = new Point(rect.x + rect.width / 2, rect.y + rect.height / 2);
                Point pt1 = new Point(center.x - 2, center.y - 2);
                Point pt2 = new Point(center.x + 2, center.y + 2);
                Imgproc.rectangle(mRgba, pt1, pt2, CONTOUR_COLOR, 5);

                // System.out.println("X: " + center.x);
                if (isTracking) {
                    if (maxVals  >  locations.size()) {
                        locations.add(center.x);
                        Log.e(TAG,"X: " + center.x);
                        timeStamps.add(System.nanoTime());
                        Log.e(TAG,"Time: " + System.nanoTime());
                    }
                    else
                    {
                        Log.e(TAG,"Max Capacity of List Reached !");
                    }
                }
            }
            //    Mat colorLabel = mRgba.submat(4, mSpectrum.rows(), 4, mSpectrum.rows());
            //    colorLabel.setTo(mBlobColorRgba);

            //    Mat spectrumLabel = mRgba.submat(4, 4 + mSpectrum.rows(), 70, 70 + mSpectrum.cols());
            //    mSpectrum.copyTo(spectrumLabel);
        }

        return mRgba;
    }

    private Scalar converScalarHsv2Rgba(Scalar hsvColor) {
        Mat pointMatRgba = new Mat();
        Mat pointMatHsv = new Mat(1, 1, CvType.CV_8UC3, hsvColor);
        Imgproc.cvtColor(pointMatHsv, pointMatRgba, Imgproc.COLOR_HSV2RGB_FULL, 4);

        return new Scalar(pointMatRgba.get(0, 0));
    }


}
