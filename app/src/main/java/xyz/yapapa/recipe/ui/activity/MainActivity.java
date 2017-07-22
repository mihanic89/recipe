package xyz.yapapa.recipe.ui.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.xdty.preference.colorpicker.ColorPickerDialog;
import org.xdty.preference.colorpicker.ColorPickerSwatch;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xyz.yapapa.recipe.R;
import xyz.yapapa.recipe.manager.FileManager;
import xyz.yapapa.recipe.manager.PermissionManager;
import xyz.yapapa.recipe.ui.component.DrawingView;
import xyz.yapapa.recipe.ui.component.SquareImageView;
import xyz.yapapa.recipe.ui.dialog.StrokeSelectorDialog;



public class MainActivity extends AppCompatActivity
{
	@Bind(R.id.main_drawing_view) DrawingView mDrawingView;
	//@Bind(R.id.main_fill_iv)    ImageView mFillBackgroundImageView;
	@Bind(R.id.main_color_iv)   SquareImageView mColorImageView;
	@Bind(R.id.main_stroke_iv) 	SquareImageView mStrokeImageView;
	@Bind(R.id.main_undo_iv)    SquareImageView mUndoImageView;
	@Bind(R.id.main_redo_iv)    SquareImageView mRedoImageView;
    @Bind(R.id.prev_pic)        SquareImageView mPrevImageView;
    @Bind(R.id.next_pic)        SquareImageView mNextImageView;
    @Bind(R.id.share)           SquareImageView mShareImageView;
    @Bind(R.id.delete)          SquareImageView mDeleteImageView;

	private int mCurrentBackgroundColor;
	private int mCurrentColor;
	private int mCurrentStroke;
    private AdView mAdView;
    private FirebaseAnalytics mFirebaseAnalytics;
	private static final int MAX_STROKE_WIDTH = 50;
    int[] intDrawables ;
    int i=0;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);


        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
		MobileAds.initialize(getApplicationContext(), "ca-app-pub-2888343178529026~2653479392");


		mAdView = (AdView) findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder()
				.addTestDevice("09D7B5315C60A80D280B8CDF618FD3DE")
				.build();
		mAdView.loadAd(adRequest);

		mAdView.setAdListener(new AdListener() {
			@Override
			public void onAdLoaded() {
				// Code to be executed when an ad finishes loading.
			}

			@Override
			public void onAdFailedToLoad(int errorCode) {
				// Code to be executed when an ad request fails.
				Log.d("ADERROR", "Error code =" + errorCode);
			}

			@Override
			public void onAdOpened() {
				// Code to be executed when an ad opens an overlay that
				// covers the screen.
			}

			@Override
			public void onAdLeftApplication() {
				// Code to be executed when the user has left the app.
			}

			@Override
			public void onAdClosed() {
				// Code to be executed when when the user is about to return
				// to the app after tapping on an ad.
			}
		});

        loadDrawables();
		ButterKnife.bind(this);
		//initBackground();
		initDrawingView();

		mDrawingView.setCustomBitmap1(BitmapFactory.decodeResource(getResources(),intDrawables[0]));
		Log.d("my", "SetCustomx0: " + BitmapFactory.decodeResource(getResources(),intDrawables[0]).getWidth());
		Log.d("my", "SetCustomy0: "+ BitmapFactory.decodeResource(getResources(),intDrawables[0]).getHeight());


	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.action_share:
				requestPermissionsAndSaveBitmap();
				break;
			case R.id.action_clear:
				mDrawingView.clearCanvas();
				break;
		}

		return super.onOptionsItemSelected(item);
	}

	private void initDrawingView()
	{
		mCurrentBackgroundColor = ContextCompat.getColor(this, android.R.color.white);
		mCurrentColor = ContextCompat.getColor(this, R.color.red);
		mCurrentStroke = 15;
		mDrawingView.setDrawingCacheEnabled(true);
        //mDrawingView.setDrawId(intDrawables[0]);

		//mDrawingView.setBackgroundResource(R.drawable.c001);

		//mDrawingView.SetCustomBitmap1(BitmapFactory.decodeResource(getResources(),intDrawables[0]));
		mDrawingView.setPaintColor(mCurrentColor);
		mDrawingView.setPaintStrokeWidth(mCurrentStroke);

		//mDrawingView.SetCustomBitmap1(BitmapFactory.decodeResource(getApplicationContext().getResources(),intDrawables[0]),mDrawingView.getWidth(),mDrawingView.getHeight());
		//mDrawingView.SetCustomBitmap1(BitmapFactory.decodeResource(getApplicationContext().getResources(),intDrawables[0]));
	}

	private Bitmap convertBitmap (int resId, Canvas canvas){
		Bitmap b= BitmapFactory.decodeResource(getResources(),resId);
		int width = b.getWidth();
		int height = b.getHeight();
		float scaleWidth = ((float) canvas.getWidth()) / width;
		float scaleHeight = ((float) canvas.getHeight()) / height;

		if (scaleWidth>scaleHeight)
		{scaleWidth=scaleHeight;}
		//else {scaleHeight=scaleWidth;}

		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);

		return Bitmap.createBitmap(b, 0, 0,
				width, height, matrix, true);

	}

	private void startFillBackgroundDialog()
	{
		int[] colors = getResources().getIntArray(R.array.palette);

		ColorPickerDialog dialog = ColorPickerDialog.newInstance(R.string.color_picker_default_title,
				colors,
				mCurrentBackgroundColor,
				4,
				ColorPickerDialog.SIZE_SMALL);

		dialog.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener()
		{

			@Override
			public void onColorSelected(int color)
			{
				mCurrentBackgroundColor = color;
				mDrawingView.setBackgroundColor(mCurrentBackgroundColor);
			}

		});

		dialog.show(getFragmentManager(), "ColorPickerDialog");
	}

	private void startColorPickerDialog()
	{
		int[] colors = getResources().getIntArray(R.array.palette);

		ColorPickerDialog dialog = ColorPickerDialog.newInstance(R.string.color_picker_default_title,
				colors,
				mCurrentColor,
				4,
				ColorPickerDialog.SIZE_SMALL);

		dialog.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener()
		{

			@Override
			public void onColorSelected(int color)
			{
				mCurrentColor = color;
				mDrawingView.setPaintColor(mCurrentColor);
			}

		});

		dialog.show(getFragmentManager(), "ColorPickerDialog");
	}

	private void startStrokeSelectorDialog()
	{
		StrokeSelectorDialog dialog = StrokeSelectorDialog.newInstance(mCurrentStroke, MAX_STROKE_WIDTH);

		dialog.setOnStrokeSelectedListener(new StrokeSelectorDialog.OnStrokeSelectedListener()
		{
			@Override
			public void onStrokeSelected(int stroke)
			{
				mCurrentStroke = stroke;
				mDrawingView.setPaintStrokeWidth(mCurrentStroke);
			}
		});

		dialog.show(getSupportFragmentManager(), "StrokeSelectorDialog");
	}

	private void startShareDialog(Uri uri)
	{
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_SEND);
		intent.setType("image/*");

		intent.putExtra(Intent.EXTRA_SUBJECT, "");
		intent.putExtra(Intent.EXTRA_TEXT, "");
		intent.putExtra(Intent.EXTRA_STREAM, uri);
		startActivity(Intent.createChooser(intent, "Share Image"));
	}

	private void requestPermissionsAndSaveBitmap()
	{
		if (PermissionManager.checkWriteStoragePermissions(this))
		{
			Uri uri = FileManager.saveBitmap(mDrawingView.getBitmap());
			startShareDialog(uri);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
	{
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		switch (requestCode)
		{
			case PermissionManager.REQUEST_WRITE_STORAGE:
			{
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
				{
					Uri uri = FileManager.saveBitmap(mDrawingView.getBitmap());
					startShareDialog(uri);
				} else
				{

					Toast.makeText(this, R.string.permission_read_write, Toast.LENGTH_LONG).show();
				}
			}
		}
	}

	/*@OnClick(R.id.main_fill_iv)
	public void onBackgroundFillOptionClick()
	{
		startFillBackgroundDialog();
	}
	*/

	@OnClick(R.id.main_color_iv)
	public void onColorOptionClick()
	{
		startColorPickerDialog();
	}

	@OnClick(R.id.main_stroke_iv)
	public void onStrokeOptionClick()
	{
		startStrokeSelectorDialog();
	}

	@OnClick(R.id.main_undo_iv)
	public void onUndoOptionClick()
	{
		mDrawingView.undo();
	}

	@OnClick(R.id.share)
	public void onShareOptionClick()
	{
        requestPermissionsAndSaveBitmap();
	}

    @OnClick(R.id.delete)
    public void onDeleteOptionClick()
    {
        mDrawingView.clearCanvaswithoutBackground();
    }

    @OnClick(R.id.main_redo_iv)
    public void onRedoOptionClick()
    {
        mDrawingView.redo();
    }

    @OnClick(R.id.prev_pic)
    public void onPrevOptionClick()
    {
        mDrawingView.clearCanvas();
        i--;
        if (i<0 )
        {i=intDrawables.length-1;
			mDrawingView.setCustomBitmap2(intDrawables[i]);}//mDrawingView.SetCustomBitmap1(BitmapFactory.decodeResource(getResources(),intDrawables[i]));}
        else mDrawingView.setCustomBitmap2(intDrawables[i]);//{mDrawingView.SetCustomBitmap1(BitmapFactory.decodeResource(getResources(),intDrawables[i]));}
    }

    @OnClick(R.id.next_pic)
    public void onNextOptionClick(){
        mDrawingView.clearCanvas();
        i++;
        if (i<intDrawables.length )
        {mDrawingView.setCustomBitmap2(intDrawables[i]);}//mDrawingView.SetCustomBitmap1(BitmapFactory.decodeResource(getResources(),intDrawables[i]));}
        else {i=0;
			mDrawingView.setCustomBitmap2(intDrawables[i]);}//mDrawingView.SetCustomBitmap1(BitmapFactory.decodeResource(getResources(),intDrawables[i]));}
    }


    private void loadDrawables() {


		int screenSize = getResources().getConfiguration().screenLayout &
				Configuration.SCREENLAYOUT_SIZE_MASK;
		if (screenSize==Configuration.SCREENLAYOUT_SIZE_XLARGE)
		{
			intDrawables = new int[]{
					R.drawable.c001,
					R.drawable.c002,
					R.drawable.c003,
					R.drawable.c004,
					R.drawable.c005,
					R.drawable.c006,
					R.drawable.c007,
					R.drawable.c008,
					R.drawable.c009,
					R.drawable.d01,
					R.drawable.d02,
					R.drawable.d03,
					R.drawable.d04,
					R.drawable.d05,
					R.drawable.d06,
					R.drawable.d07,
					R.drawable.d08,
					R.drawable.d09,
					R.drawable.d10,
					R.drawable.d11,
					R.drawable.d12,
					R.drawable.d13,
					R.drawable.d14,
					R.drawable.d15,
					R.drawable.d16,
					R.drawable.d17,
					R.drawable.d18,
					R.drawable.d19,
					R.drawable.d20,
					R.drawable.d21,
					R.drawable.d22,
					R.drawable.d23,
					R.drawable.d24,
					R.drawable.d25,
					R.drawable.d26,
					R.drawable.d27,
					R.drawable.b01,
					R.drawable.b02,
					R.drawable.b03,
					R.drawable.b04,
					R.drawable.b05,
					R.drawable.b06,
					R.drawable.b07,
					R.drawable.b08,
					R.drawable.d28,
					R.drawable.d29,
					R.drawable.d30,
					R.drawable.d31,
					R.drawable.d32,
					R.drawable.d33,
					R.drawable.d34,
					R.drawable.d35,
			};
		}
		else {
			intDrawables = new int[]{
					R.drawable.c001,
					R.drawable.c002,
					R.drawable.c003,
					R.drawable.c004,
					R.drawable.c005,
					R.drawable.c006,
					R.drawable.c007,
					R.drawable.c008,
					R.drawable.c009,
					R.drawable.d01,
					R.drawable.d02,
					R.drawable.d03,
					R.drawable.d04,
					R.drawable.d05,
					R.drawable.d06,
					R.drawable.d07,
					R.drawable.d08,
					R.drawable.d09,
					R.drawable.d10,
					R.drawable.d11,
					R.drawable.d12,
					R.drawable.d13,
					R.drawable.d14,
					R.drawable.d15,
					R.drawable.d16,
					R.drawable.d17,
					R.drawable.d18,
					R.drawable.d19,
					R.drawable.d20,
					R.drawable.d21,
					R.drawable.d22,
					R.drawable.d23,
					R.drawable.d24,
					R.drawable.d25,
					R.drawable.d26,
					R.drawable.d27
			};
		}
    }

	/** Called when leaving the activity */
	@Override
	public void onPause() {
		if (mAdView != null) {
			mAdView.pause();
		}
		super.onPause();
	}

	/** Called when returning to the activity */
	@Override
	public void onResume() {
		super.onResume();
		if (mAdView != null) {
			mAdView.resume();
		}
	}

	/** Called before the activity is destroyed */
	@Override
	public void onDestroy() {
		if (mAdView != null) {
			mAdView.destroy();
		}
		super.onDestroy();
	}
}
