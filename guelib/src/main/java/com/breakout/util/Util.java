package com.breakout.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Rect;
import android.os.Debug;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.breakout.util.data.SharedStorage;
import com.breakout.util.img.ImageLoader;
import com.breakout.util.net.BaseNet;
import com.breakout.util.string.StringUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Util class
 *
 * @author gue
 * @version 1.0
 * @copyright Copyright.2011.gue.All rights reserved.
 * @history <ol>
 * <li>변경자/날짜 : 변경사항</li>
 * </ol>
 * @since 2012. 6. 28.
 */
public final class Util {
    private final static String TAG = "Util";
//	private final static Locale DEFAULT_LOCALE = Locale.getDefault();

    private Util() {
    }

	
/* ************************************************************************************************
 * INFO view & window size
 */

    /**
     * view의 size를 log로 출력한다.
     *
     * @author gue
     * @since 2014. 1. 26.
     */
    public final static void logViewSize(final View view) {
        if (view == null) return;
        view.post(new Runnable() {
            @Override
            public void run() {
                try {
                    Rect rect = new Rect();
                    view.getGlobalVisibleRect(rect);
                    int xy[] = new int[2];
                    view.getLocationOnScreen(xy);
                    ViewGroup.LayoutParams params = view.getLayoutParams();
                    boolean paramsNull = params == null ? true : false;
                    Log.i(TAG, String.format("-- View Name : %s----------------------------------------- \n" +
                                    "|  getGlobalVisibleRect : %s \n" +
                                    "|  getLocationOnScreen : x=%d, y=%d \n" +
                                    "|  getWidth()=%d, getHeight()=%d \n" +
                                    "|  getLeft()=%d, getTop()=%d, getRight()=%d, getBottom()=%d \n" +
                                    "|  LayoutParams : %s \n" +
                                    "|  LayoutParams.width=%d, LayoutParams.height=%d, LayoutParams.weight=%f \n" +
                                    "------------------------------------------------------------",
                            view,
                            rect,
                            xy[0], xy[1],
                            view.getWidth(), view.getHeight(),
                            view.getLeft(), view.getTop(), view.getRight(), view.getBottom(),
                            params,
                            paramsNull ? params.width : 0, paramsNull ? params.height : 0, paramsNull && view instanceof LinearLayout ? ((LinearLayout.LayoutParams) params).weight : 0
                    ));
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        });
    }

    /**
     * 뷰의 계층구조를 입력된 view를 기준으로 log로 출력한다.
     *
     * @author gue
     * @since 2012. 12. 28.
     */
    public final static void logViewHierachy(View view, int step) {
        String margin = "| ";
        for (int i = 0; i < step; i++) {
            margin += "  ";
        }
        if (view instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) view;
            Log.w(TAG, margin + "step-" + step + ". view group name : " + view + "/ child count=" + vg.getChildCount());
            for (int i = 0; i < vg.getChildCount(); i++) {
                View child = vg.getChildAt(i);
                String log = margin + "#parent : step-" + step + ", child-" + i + ". " + child;
                if (child instanceof ViewGroup) {
                    Log.d(TAG, log + " / child count=" + ((ViewGroup) child).getChildCount());
                    logViewHierachy(child, step + 1);
                } else Log.d(TAG, log);
            }
        } else {
            Log.w(TAG, margin + "step-" + step + ". view name : " + view);
        }
    }
    
    
/* ************************************************************************************************
 * INFO shortcut
 */

    /**
     * short cut create
     *
     * @param context
     * @author gue
     * @since 2012. 9. 4.
     */
    public final static void addShortcut(Context context, int iconId, String appName) {
        /*Intent intentUninstall = new Intent();
		intentUninstall.setAction("com.android.launcher.action.UNINSTALL_SHORTCUT"); 
		context.sendBroadcast(intentUninstall);*/

        Intent shortcutIntent = new Intent(Intent.ACTION_MAIN);
        shortcutIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        shortcutIntent.setClassName(context, context.getClass().getName());
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

        Intent intent = new Intent();
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, appName);
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(context, iconId));
        intent.putExtra("duplicate", false);
        intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        context.sendBroadcast(intent);
    }

	
/* ************************************************************************************************
 * INFO app exit & intent method
 */

    /**
     * 다른 app으로의 intent를 사용할때 해당 intent가 가능한지에 대한 check
     *
     * @author gue
     * @since 2012. 8. 26.
     */
    public final static boolean isAvailableIntent(Context context, Intent intent) {
        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list != null && list.size() > 0;
    }

    /**
     * onNewIntent()에서 extra name이 CValue.EX_EXIT 값이 true라면
     * 이것을 받은 actvity는 stack상에서 최상위에 존재하고 유일한 activity가 된다.
     * application의 종료를 위하여 finish()를 선언한다.<br>
     * <pre>
     * ex)
     * boolean exit = intent.getBooleanExtra(Const.EX_EXIT, false);
     * if (exit) {
     * 	super.finish();
     * }
     * </pre>
     * 외부에서의 유입등으로 인하여 baseActivity가 현재 application의 package가 아닐경우 제대로 된 처리가 될 수 없다.
     * 해당 상황을 막기 위해 Manifest에 android:launchMode="singleTask" 을 통하여 공유나 scheme를 통한 유입을 허용하여
     * baseActivity에 다른 application의 package가 오지 않도록 주위하여 작업해야 한다.
     *
     * @param act
     * @author gue
     * @since 2011. 6. 15.
     */
    public final static void appExit(Context context) {
        final ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final String packageName = context.getPackageName();
        List<RunningTaskInfo> taskList = am.getRunningTasks(1);
        String baseClassName = taskList.get(0).baseActivity.getClassName();
        String topClassName = taskList.get(0).topActivity.getClassName();
        int numActivity = taskList.get(0).numActivities;
        int numRun = taskList.get(0).numRunning;
        Log.w(TAG, String.format("app exit : base-%s / top-%s / numAct-%d / numRun-%d",
                baseClassName, topClassName, numActivity, numRun));

        if (StringUtil.nullCheck(packageName) != null && packageName.equals(taskList.get(0).baseActivity.getPackageName())) {
            // stack에 activity가 1개 초과일 때
            if (numActivity > 1) {
                Intent home = new Intent();
                home.setClassName(context, baseClassName);
                home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                home.putExtra(CValue.EX_EXIT, true);
                context.startActivity(home);
            }
            // stack에 현재 activity 한개 뿐일 때
            else {
                ((Activity) context).finish();
            }
        }
        // 타 어플에서 진입한 경우, 이경우는 base의 package가 다른경우 생길수 있는데 현재는 사용하지 않음.
        else {
            if (numActivity > 2) {
                Intent top = new Intent();
                top.setClassName(context, topClassName);
                top.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                top.putExtra(CValue.EX_EXIT_SHARE, true);
                context.startActivity(top);
            } else {
                ((Activity) context).finish();
            }
        }

        // instansce destroy & cache delete
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                List<RunningAppProcessInfo> procesesList = am.getRunningAppProcesses();
                for (RunningAppProcessInfo info : procesesList) {
                    if (packageName != null && packageName.equals(info.processName)) {
                        List<RunningTaskInfo> taskList = am.getRunningTasks(procesesList.size());
                        boolean aliveAppTask = false;
                        for (RunningTaskInfo task : taskList) {
                            if (packageName.equals(task.baseActivity.getPackageName())) {
                                aliveAppTask = true;
                            }
                        }
                        if (!aliveAppTask) {
                            SharedStorage.destroyInstance();
                            ImageLoader.destroyInstance();
                            BaseNet.destroyInstance();
//							am.killBackgroundProcesses(packageName);
                        }
                        break;
                    }
                }
            }
        }, 1000);
    }

    /**
     * onNewIntent()에서 extra name이 CValue.EX_FORCE_MOVE이고 값이 true라면
     * 이것을 받은 actvity는 stack상에서 최상위에 존재하고 유일한 activity가 된다.
     * 강제 이동 처리를 위하여 모든 stack을 정리하기 위한 것이니 해당 activity에서 이동할 activity에 대한 intent를
     * onNewIntent()에 정의하여 처리하여 준다.<br>
     * <pre>
     * ex)
     * boolean force_move = intent.getBooleanExtra(Const.EX_FORCE_MOVE, false);
     * if (force_move) {
     * 	Intent home = new Intent(_context, LoginActivity.class);
     * 	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
     * 	startActivity(home);
     * 	super.finish();
     * }
     * </pre>
     * 외부에서의 유입등으로 인하여 baseActivity가 현재 application의 package가 아닐경우 제대로 된 처리가 될 수 없다.
     * 해당 상황을 막기 위해 Manifest에 android:launchMode="singleTask" 을 통하여 공유나 scheme를 통한 유입을 허용하여
     * baseActivity에 다른 application의 package가 오지 않도록 주위하여 작업해야 한다.
     *
     * @param act
     * @author gue
     * @since 2011. 6. 15.
     */
    public final static void forceMove(Context context) {
        final ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> taskList = am.getRunningTasks(1);
        String baseClassName = taskList.get(0).baseActivity.getClassName();
        String topClassName = taskList.get(0).topActivity.getClassName();
        int numActivity = taskList.get(0).numActivities;
        int numRun = taskList.get(0).numRunning;
        Log.w(TAG, String.format("app forceMove : base-%s / top-%s / numAct-%d / numRun-%d",
                baseClassName, topClassName, numActivity, numRun));

        Intent home = new Intent();
        home.setClassName(context, taskList.get(0).baseActivity.getClassName());
        home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        home.putExtra(CValue.EX_FORCE_MOVE, true);
        context.startActivity(home);
    }

    /**
     * process 강제 종료
     * @author gue
     * @since 2011. 6. 15.
     */
/*	protected final void appExit1(){
		final ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		// stop running service inside current process.
		List<android.app.ActivityManager.RunningServiceInfo> serviceList = am.getRunningServices(100);
		for (android.app.ActivityManager.RunningServiceInfo service : serviceList) {
			if (service.pid == android.os.Process.myPid()) {
				Intent stop = new Intent();
				stop.setComponent(service.service);
				stopService(stop);
			}
		}
		// move current task to background.
		Intent launchHome = new Intent(Intent.ACTION_MAIN);
		launchHome.addCategory(Intent.CATEGORY_DEFAULT);
		launchHome.addCategory(Intent.CATEGORY_HOME);
		startActivity(launchHome);
		// post delay runnable(waiting for home application launching)
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				am.killBackgroundProcesses(getPackageName());
			}
		}, 3000);
	}*/
	
	
/* ************************************************************************************************
 * INFO os & memory & file
 */

    /**
     * for check heap size & system gc
     *
     * @param TAG    application name
     * @param gcFlag true 일때 gc 수행
     * @author gue
     * @since 2011 9. 19.
     */
    public final static void showHeap(String TAG, boolean gcFlag) {
        double heapSize = Debug.getNativeHeapSize() / 1048576.0;
        double heapFreeSize = Debug.getNativeHeapFreeSize() / 1048576.0;
        double heapAllocatedSize = Debug.getNativeHeapAllocatedSize() / 1048576.0;
        double totalMemory = Runtime.getRuntime().totalMemory() / 1048576.0;
        double maxMemory = Runtime.getRuntime().maxMemory() / 1048576.0;
        double freeMemory = Runtime.getRuntime().freeMemory() / 1048576.0;
        double allocatedMemory = totalMemory - freeMemory;
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);
        Log.d(TAG, String.format("check memory ----------------------------------------\n" +
                        "  Native Heap size : %s\n" +
                        "  Native HeapFree size : %s\n" +
                        "  Native HeapAllocated size : %s\n" +
                        "  Total Memory size : %sMB\n" +
                        "  Max Memory size : %sMB\n" +
                        "  Free Memory size : %sMB\n" +
                        "  Allocation Memory : %sMB\n" +
                        "---------------------------------------------------",
                df.format(heapSize), df.format(heapFreeSize), df.format(heapAllocatedSize),
                df.format(totalMemory), df.format(maxMemory), df.format(freeMemory), df.format(allocatedMemory)));
        if (gcFlag) System.gc(); 
/*        // don't need to add the following lines, it's just an app specific handling in my app
		final int MEMORY_BUFFER_LIMIT_FOR_RESTART = 15;
        if (allocated>=(new Double(Runtime.getRuntime().maxMemory())/new Double((1048576))-MEMORY_BUFFER_LIMIT_FOR_RESTART)) { 
            android.os.Process.killProcess(android.os.Process.myPid()); 
        }*/
    }

    /**
     * memory recycle <br>
     * blog url : <a href=http://givenjazz.tistory.com/48>http://givenjazz.tistory.com/48<a>
     *
     * @author givenjazz
     */
    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public static void recursiveRecycle(View root) {
        if (root == null) return;
        root.destroyDrawingCache();        // add from gue
        if (android.os.Build.VERSION.SDK_INT >= 16) root.setBackground(null);
        else root.setBackgroundDrawable(null);
        if (root instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) root;
            int count = group.getChildCount();
            for (int i = 0; i < count; i++) recursiveRecycle(group.getChildAt(i));
            if (!(root instanceof AdapterView)) group.removeAllViews();
        }
        if (root instanceof ImageView) ((ImageView) root).setImageDrawable(null);
        root = null;
    }

    /**
     * SDCard mount ckeck
     *
     * @author gue
     * @since 2012. 7. 2.
     */
    public final static boolean isExternalStorageAvailable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * show memory size
     *
     * @author gue
     * @since 2012. 7. 2.
     */
    public final static void ShowMemorySize() {
        Log.i("MemoryStatus", String.format("MemoryStatus -----------------------------\n" +
                        " Total Internal MemorySize : %s\n" +
                        " Available Internal MemorySize : %s\n" +
                        " Total External MemorySize : %s\n" +
                        " Available External MemorySize : %s\n" +
                        "------------------------------------------",
                StringUtil.convertFileSizeFormat(getTotalInternalMemorySize()),
                StringUtil.convertFileSizeFormat(getAvailableInternalMemorySize()),
                StringUtil.convertFileSizeFormat(getTotalExternalMemorySize()),
                StringUtil.convertFileSizeFormat(getAvailableExternalMemorySize())));
    }

    /**
     * @author gue
     * @since 2012. 7. 2.
     */
    public final static long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }

    /**
     * @author gue
     * @since 2012. 7. 2.
     */
    public final static long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    /**
     * @author gue
     * @since 2012. 7. 2.
     */
    public final static long getTotalExternalMemorySize() {
        if (isExternalStorageAvailable() == true) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return totalBlocks * blockSize;
        }
        return -1;
    }

    /**
     * @author gue
     * @since 2012. 7. 2.
     */
    public final static long getAvailableExternalMemorySize() {
        if (isExternalStorageAvailable() == true) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return availableBlocks * blockSize;
        }
        return -1;
    }


    /**
     * 파일을 쓰고 읽고 다시 쓴후 삭제하는 행동을 하여 sdcard의 읽고 쓰는부분에 문제가 있는지 check한다. 문제가 있다면 Exception 발생
     *
     * @author gue
     * @since 2013. 1. 22.
     */
    public final static void writeTest() throws Exception {
        final File f = new File(Environment.getExternalStorageDirectory(), "/garbege.txt");
        if (f.exists() && !f.delete()) throw new IOException("don't acess");

        FileWriter fw = new FileWriter(f);
        fw.write("0123456789");
        fw.close();

        BufferedReader br = new BufferedReader(new FileReader(f));
        String read = br.readLine();
        br.close();

        fw = new FileWriter(f);
        fw.write(read);
        fw.close();

        if (f.exists() && !f.delete()) throw new IOException("don't acess");
    }


    /**
     * 작업중..<p>
     * app의 남은 용량을 log를 사용하여 보여주며 특정 DTO class로 리턴한다.
     *
     * @param context application context
     * @author gue
     * @since 2013. 1. 23.
     */
    public final static void checkSdCardCapacity(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                File cacheDir = null;
                if (context.getExternalCacheDir() != null) {
                    cacheDir = context.getExternalCacheDir();
                } else {
                    cacheDir = context.getCacheDir();
                }
                File[] child = cacheDir.listFiles();

                System.out.println("hhhh folder path : " + cacheDir.getAbsolutePath());
                System.out.println("hhhh file num : " + child.length);

                long total = 0;
                for (File f : child) {
                    if (f.isFile()) {
                        total += f.length();
                    }
                }
                System.out.println("hhhh folder size : " + total);
                System.out.println("hhhh folder size : " + StringUtil.convertFileSizeFormat(total));
//				Log.d(TAG, String.format(	"-- View Name : %s\n-----------------------------------------" +
//											"|  getWidth()=%d, getHeight()=%d\n" +
//											"|  LayoutParams : s\n" +
//											"|  LayoutParams.width=%d, LayoutParams.height=%d, LayoutParams.weight=%d\n" +
//											"-------------------------------------------------------",
//											view,
//											view.getWidth(), view.getHeight(),
//											params,
//											paramsNull ? params.width : 0, paramsNull ? params.height : 0,
//													paramsNull && view instanceof LinearLayout ? ((LinearLayout.LayoutParams) params).weight : 0 
//											));
            }
        }).start();

    }

}