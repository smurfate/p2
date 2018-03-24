package mp3ready.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import mp3ready.lazylist.FileCache;
import mp3ready.ui.MainActivity;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


public class Utilities {

	public static Typeface getTypefaceRegular(Context context) {

		return Typeface.createFromAsset(context.getAssets(),
				"Roboto-Regular.ttf");

	}

	public static Typeface getTypefaceMedium(Context context) {

		return Typeface.createFromAsset(context.getAssets(),
				"Roboto-Medium.ttf");

	}

	/**
	 * Returns a pseudo-random number between min and max, inclusive. The
	 * difference between min and max can be at most
	 * <code>Integer.MAX_VALUE - 1</code>.
	 * 
	 * @param min
	 *            Minimum value
	 * @param max
	 *            Maximum value. Must be greater than min.
	 * @return Integer between min and max, inclusive.
	 * @see java.util.Random#nextInt(int)
	 */
	public static int randInt(int min, int max) {

		// NOTE: Usually this should be a field rather than a method
		// variable so that it is not re-seeded every call.
		Random rand = new Random();

		// nextInt is normally exclusive of the top value,
		// so add 1 to make it inclusive
		int randomNum = rand.nextInt((max - min) + 1) + min;

		return randomNum;
	}

	public static String ConvertToMinutes(int time) {
		String duration;

		int seconds = (int) (time / 1000) % 60;
		int minutes = (int) ((time / (1000 * 60)) % 60);

		if (seconds < 10) {
			if (minutes < 10) {
				duration = "0" + Integer.toString(minutes) + ":0"
						+ Integer.toString(seconds);
			} else {
				duration = Integer.toString(minutes) + ":0"
						+ Integer.toString(seconds);
			}

		} else {
			if (minutes < 10) {
				duration = "0" + Integer.toString(minutes) + ":"
						+ Integer.toString(seconds);
			} else {
				duration = Integer.toString(minutes) + ":"
						+ Integer.toString(seconds);
			}
		}

		return duration;
	}

	public static int[] getScreenSizePixels(MainActivity mainActivity) {
		int widthHeightInPixels[] = { 0, 0 };
		Resources resources = mainActivity.getResources();
		Configuration config = resources.getConfiguration();
		DisplayMetrics dm = resources.getDisplayMetrics();
		// Note, screenHeightDp isn't reliable
		// (it seems to be too small by the height of the status bar),
		// but we assume screenWidthDp is reliable.
		// Note also, dm.widthPixels,dm.heightPixels aren't reliably pixels
		// (they get confused when in screen compatibility mode, it seems),
		// but we assume their ratio is correct.
		double screenWidthInPixels = (double) config.screenWidthDp * dm.density;
		double screenHeightInPixels = screenWidthInPixels * dm.heightPixels
				/ dm.widthPixels;
		widthHeightInPixels[0] = (int) (screenWidthInPixels + .5);
		widthHeightInPixels[1] = (int) (screenHeightInPixels + .5);
		return widthHeightInPixels;
	}

	public static void getScreeSizeinInch(MainActivity mainActivity) {

		DisplayMetrics dm = new DisplayMetrics();
		mainActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;
		int height = dm.heightPixels;
		int dens = dm.densityDpi;
		double wi = (double) width / (double) dens;
		double hi = (double) height / (double) dens;
		double x = Math.pow(wi, 2);
		double y = Math.pow(hi, 2);
		double screenInches = Math.sqrt(x + y);
	}

	// open the browser for url
	public static void browseURL(Activity context, String url) {
		Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url));
		context.startActivity(intent);
	}

	public static Bitmap createDrawableFromView(Context context, View view) {
		DisplayMetrics displayMetrics = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(displayMetrics);
		view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
		view.layout(0, 0, displayMetrics.widthPixels,
				displayMetrics.heightPixels);
		view.buildDrawingCache();
		Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(),
				view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmap);
		view.draw(canvas);

		return bitmap;
	}

	// this function returns the resources id for the passed resource name
	public static Integer getResourceByURI(Activity context, String uri) {
		// returns 0 if the resource doesn't exists
		Integer result = 0;
		result = context.getResources().getIdentifier(uri, null,
				context.getPackageName());
		return result;
	}

	// this function returns list of installed apps
	public static ArrayList<PackageInfo> getInstalledPackagesInfo(
			Activity context, boolean getSysPackages) /*
													 * false = no system
													 * packages
													 */
	{
		ArrayList<PackageInfo> result = new ArrayList<PackageInfo>();
		List<PackageInfo> installedPackagesInfo = context.getPackageManager()
				.getInstalledPackages(0);
		for (int i = 0; i < installedPackagesInfo.size(); i++) {
			PackageInfo packageInfo = installedPackagesInfo.get(i);
			if (!getSysPackages && packageInfo.versionName == null) {
				continue;
			} else if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1)// don't
																								// get
																								// system
																								// apps
			{
				continue;
			}
			result.add(packageInfo);
		}
		return result;
	}

	public static void openURL(Context ctx, String url) {
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		ctx.startActivity(browserIntent);
	}

	public static Intent newTwitterIntent(PackageManager pm, String url) {
		Uri uri;
		try {
			pm.getPackageInfo("com.twitter.android", 0);
			uri = Uri.parse("fb://facewebmodal/f?href=" + url);
		} catch (PackageManager.NameNotFoundException e) {
			uri = Uri.parse(url);
		}
		return new Intent(Intent.ACTION_VIEW, uri);
	}

	/**
	 * <p>
	 * open pdf file
	 * </p>
	 * 
	 * @param ctx
	 *            current activity
	 * @param file
	 *            pdf file path in fils in device
	 */
	public static void openPdfFile(Context ctx, File file) {

		Uri path = Uri.fromFile(file);
		Intent pdfOpenintent = new Intent(Intent.ACTION_VIEW);
		pdfOpenintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		pdfOpenintent.setDataAndType(path, "application/pdf");
		try {
			ctx.startActivity(pdfOpenintent);
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
			Toast.makeText(ctx, "pdf viewer didnt'installed", Toast.LENGTH_LONG)
					.show();
		}
	}

	public static long getTimestampOfFirstDayOfCurrentYear() {
		try {
			Calendar calendar = Calendar.getInstance();
			int year = calendar.get(Calendar.YEAR);
			String str_date = "18-02-" + year;
			SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
			Date date = (Date) formatter.parse(str_date);
			return date.getTime();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return 0;
		}

	}

	/**
	 * <p>
	 * display image in image view
	 * 
	 * @param ctx
	 *            current activity
	 * @param imgView
	 *            image view that hold the image
	 * @param url
	 *            the url of image that we want to download it into imgView
	 */
	public static void renderImage(Context ctx, ImageView imgView, String url) {
		imgView.setImageDrawable(null);
		File cacheDir = new File(
				android.os.Environment.getExternalStorageDirectory(),
				".FleetCache");
		File filepath = FileCache.getFileFrom(url, cacheDir);
		Bitmap img = initImgFromPath(filepath);
		if (img == null) {
			Log.e("Utilities", "getting it from url");
			// ((MainActivity)ctx).imageLoader.DisplayImage(url, imgView);
		} else {
			imgView.setImageBitmap(img);
		}

	}

	/**
	 * <p>
	 * create bitmap from image file
	 * </p>
	 * 
	 * @param filepath
	 *            the path of image file
	 * @return bitmap created from filepath
	 */
	public static Bitmap initImgFromPath(File filepath) {
		Bitmap myBitmap = null;
		try {
			if (filepath.exists()) {
				myBitmap = BitmapFactory.decodeFile(filepath.getAbsolutePath());
				// myBitmap = decodeFile(imgFile , 200 ,200);
				if (myBitmap != null) {
					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					myBitmap.compress(Bitmap.CompressFormat.JPEG, 60, stream);

				} else {
					Log.e("Utilities", "BITMAP " + filepath.getAbsolutePath()
							+ " IS NULL");
				}
			} else {
				Log.e("Utilities", "FILE " + filepath.getAbsolutePath()
						+ " NOT EXISTS");
			}
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		} catch (Error e) {
			e.printStackTrace();
		}
		return myBitmap;
	}

	public static String chngEnNum2ArNum(String content) {
		content = content.replace('1', '١').replace('2', '٢').replace('3', '٣')
				.replace('4', '٤').replace('5', '٥').replace('6', '٦')
				.replace('7', '٧').replace('8', '٨').replace('9', '٩')
				.replace('0', '٠').replace("10", "١٠").replace("11", "١١")
				.replace("12", "١٢").replace("13", "١٣").replace("14", "١٤")
				.replace("15", "١٥").replace("16", "١٦").replace("17", "١٧")
				.replace("18", "١٨").replace("19", "١٩").replace("20", "٢٠");

		return content;
	}

	public static boolean isNumeric(String str) {
		try {
			Integer.parseInt(str);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	public static String getNxtSat(String currSat) {
		String nxtSat = currSat;
		SimpleDateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");
		try {
			Date date = (Date) df1.parse(currSat);
			long interval = 24 * 1000 * 60 * 60; // 1 hour in millis
			long curTime = date.getTime();
			int i = 0;
			while (i <= 7) {
				nxtSat = df1.format(new Date(curTime));
				Log.i("weekly date", nxtSat);
				curTime += interval;
				i++;
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return nxtSat;
	}

	public static String getNxtThur(String currSat) {
		String nxtSat = currSat;
		SimpleDateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");
		try {
			Date date = (Date) df1.parse(currSat);
			long interval = 24 * 1000 * 60 * 60; // 1 hour in millis
			long curTime = date.getTime();
			int i = 1;
			while (i < 7) {
				nxtSat = df1.format(new Date(curTime));
				curTime += interval;
				i++;
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return nxtSat;
	}

	public static String getPrevSat(String currSat) {
		String prevSat = currSat;
		SimpleDateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");
		try {
			Date date = (Date) df1.parse(currSat);
			long interval = 24 * 1000 * 60 * 60; // 1 hour in millis
			long curTime = date.getTime();
			int i = 7;
			while (i >= 0) {
				prevSat = df1.format(new Date(curTime));
				Log.i("weekly date", prevSat);
				curTime -= interval;
				i--;
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return prevSat;
	}

	public static List<String> getDates(String dateString1, String dateString2) {
		List<String> dates = new ArrayList<String>();
		SimpleDateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");

		Date date1 = null;
		Date date2 = null;

		try {
			date1 = (Date) df1.parse(dateString1);
			date2 = (Date) df1.parse(dateString2);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		long interval = 24 * 1000 * 60 * 60; // 1 hour in millis
		long endTime = date2.getTime(); // create your endtime here, possibly
										// using Calendar or Date
		long curTime = date1.getTime();
		while (curTime <= endTime) {
			dates.add(df1.format(new Date(curTime)));
			curTime += interval;
		}
		// Calendar cal1 = df1.getCalendar();
		// cal1.setTime(date1);
		//
		// Calendar cal2 = df1.getCalendar();
		// cal2.setTime(date2);
		//
		//
		// while (!cal1.after(cal2)) {
		// dates.add(cal1.getTime().toString());
		// cal1.add(Calendar.DAY_OF_MONTH, 1);
		// }
		return dates;
	}
}
