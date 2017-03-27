package xyz.yapapa.recipe.ui.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
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

import static xyz.yapapa.recipe.R.id.adView;


public class MainActivity extends AppCompatActivity
{
	@Bind(R.id.main_drawing_view)
	DrawingView mDrawingView;
	//@Bind(R.id.main_fill_iv)    ImageView mFillBackgroundImageView;
	@Bind(R.id.main_color_iv)
	ImageView mColorImageView;
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
        mAdView = (AdView) findViewById(adView);
        AdRequest adRequest = new AdRequest.Builder()

                .build();
        mAdView.loadAd(adRequest);
        loadDrawables();
		ButterKnife.bind(this);

		initDrawingView();
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
		mDrawingView.setBackgroundResource(R.drawable.p01);//set the back ground if you wish to
		mDrawingView.setPaintColor(mCurrentColor);
		mDrawingView.setPaintStrokeWidth(mCurrentStroke);

	}

	private void startFillBackgroundDialog()
	{
		int[] colors = getResources().getIntArray(R.array.palette);

		ColorPickerDialog dialog = ColorPickerDialog.newInstance(R.string.color_picker_default_title,
				colors,
				mCurrentBackgroundColor,
				5,
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
				5,
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
			Uri uri = FileManager.saveBitmap(mDrawingView.getBitmap(getResources().getDrawable(intDrawables[i])));
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
					Uri uri = FileManager.saveBitmap(mDrawingView.getBitmap(getResources().getDrawable(intDrawables[i])));
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
        mDrawingView.clearCanvas();
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
            mDrawingView.setBackgroundResource(intDrawables[i]);}
        else {mDrawingView.setBackgroundResource(intDrawables[i]);}
    }

    @OnClick(R.id.next_pic)
    public void onNextOptionClick(){
        mDrawingView.clearCanvas();
        i++;
        if (i<intDrawables.length )
        {mDrawingView.setBackgroundResource(intDrawables[i]);}
        else {i=0;
            mDrawingView.setBackgroundResource(intDrawables[i]);}
    }


    private void loadDrawables() {
        intDrawables = new int[]{
				R.drawable.p01,

                R.drawable.p02,

                R.drawable.p03,

                R.drawable.p04,

                R.drawable.p05,

                R.drawable.p06,

                R.drawable.p07,

                R.drawable.p08,

                R.drawable.p09,

				R.drawable.p10,


        };
    }

	@Override
	public void onResume() {
		super.onResume();

		// Resume the AdView.
		mAdView.resume();
	}

	@Override
	public void onPause() {
		// Pause the AdView.
		mAdView.pause();

		super.onPause();
	}

	@Override
	public void onDestroy() {
		// Destroy the AdView.
		mAdView.destroy();

		super.onDestroy();
	}
}
