package com.breakout.sample;

import android.Manifest;
import android.app.Dialog;
import android.app.WallpaperManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.breakout.sample.constant.Const;
import com.breakout.sample.constant.Extra;
import com.breakout.sample.constant.ReceiverName;
import com.breakout.sample.constant.SharedData;
import com.breakout.sample.controller.AccountController;
import com.breakout.sample.databinding.UiBaseLayoutBinding;
import com.breakout.sample.device.speech.STTHelper;
import com.breakout.sample.device.speech.TTSHelper;
import com.breakout.sample.dto.UserDto;
import com.breakout.sample.dto.data.User;
import com.breakout.sample.fcm.MyFirebaseMessagingService;
import com.breakout.sample.ui.IntroActivity;
import com.breakout.sample.ui.MainActivity;
import com.breakout.sample.util.GoogleSignInHelper;
import com.breakout.sample.views.AppBar;
import com.breakout.sample.views.LoginDialog;
import com.breakout.sample.views.SlideMenuLayout;
import com.breakout.util.AppCompatActivityEx;
import com.breakout.util.FragmentEx;
import com.breakout.util.img.ImageLoader;
import com.breakout.util.widget.DialogView;
import com.breakout.util.widget.ViewUtil;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.Status;
import com.google.firebase.analytics.FirebaseAnalytics;


/**
 * Base Activity
 *
 * @author sung-gue
 * @version 1.0 (2013-11-27)
 */
public abstract class BaseActivity extends AppCompatActivityEx implements SlideMenuLayout.OnSlideMenuClickListener, GoogleSignInHelper.GoogleLoginHelperListener {
    protected SharedData _shared;
    protected ImageLoader _imageLoader;
    /**
     * {@link android.util.DisplayMetrics#density}
     */
    protected float _density;


    /*
        INFO: firebase analytics
     */
    private FirebaseAnalytics _firebaseAnalytics;

    private void initFirebaseAnalytics() {
        _firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        analyticsRecordScreen(_firebaseAnalytics);
    }

    /**
     * {@link #_firebaseAnalytics} 를 사용하여 화면 기록<br/>
     * Activity의 생명주기에 해당하는 {@link #onCreate(Bundle)}, {@link #onResume()} 에서 호출
     */
    protected void analyticsRecordScreen(FirebaseAnalytics firebaseAnalytics) {
    }


    /*
        INFO: life cycle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFirebaseAnalytics();
        _density = getResources().getDisplayMetrics().density;

        // init field
        _shared = SharedData.getInstance(_appContext);
        _imageLoader = ImageLoader.getInstance(_appContext);
        _imageLoader.setsdErrStr(getString(R.string.al_sdcard_strange_condition));

        checkFromURI();
//        initTTS();
//        initSTT();
        initGoogleSignIn();
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkGoogleSignIn();
    }

    @Override
    protected void onResume() {
        super.onResume();
        analyticsRecordScreen(_firebaseAnalytics);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 유효한 데이터를 불러오지 못할 경우 activity 종료
     */
    protected void finishToast() {
        finishToast(getString(R.string.al_not_match_data));
    }

    protected void finishToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        finish();
    }


    /*
        INFO: check uri
     */
    /**
     * @see Extra#EX_URI_SCHEME_HOST
     */
    public String _uriSchemeHost;
    /**
     * @see Extra#EX_URI_MSG
     */
    public String _uriMsg;
    public boolean _isFromDeepLinks;

    private void checkFromURI() {
        Intent intent = getIntent();
        Uri uri = intent.getData();
        if (uri != null) {
            _isFromDeepLinks = true;
            _uriSchemeHost = uri.getQueryParameter(Extra.EX_URI_SCHEME_HOST);
            try {
                _uriMsg = java.net.URLDecoder.decode(uri.getQueryParameter(Extra.EX_URI_MSG), "UTF-8");
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        String uriSchemeHost = intent.getStringExtra(Extra.EX_URI_SCHEME_HOST);
        String uriMsg = intent.getStringExtra(Extra.EX_URI_MSG);
        if (!TextUtils.isEmpty(uriSchemeHost)) {
            _uriSchemeHost = uriSchemeHost;
        }
        if (!TextUtils.isEmpty(uriMsg)) {
            _uriMsg = uriMsg;
        }
        if (Const.DEBUG) {
            if (uri != null || !TextUtils.isEmpty(uriMsg)) {
                Log.i(String.format("\n---------------" +
                                "\n| [%s] | from custom scheme uri (deep links)" +
                                "\n| uri                : %s" +
                                "\n| scheme host        : %s" +
                                "\n| msg                : %s" +
                                "\n| extra scheme host  : %s" +
                                "\n| extra msg          : %s" +
                                "\n-------------------",
                        TAG, uri, _uriSchemeHost, _uriMsg, uriSchemeHost, uriMsg));
            }
        }
        checkFromURI(uri);
    }

    /**
     * uri 체크후 scheme_host, msg를 기본으로 뽑는다.
     * uri 데이터를 사용하여면 override하여 사용한다.
     * <p>
     * 이 부분이 실행되는 시점은 {@link #onCreate(Bundle)} 에서 실행된다.<br>
     * 강제성이 없는 부분이기 때문에 현재는 protected method이지만 uri로의 진입등이 많아진다면 abstract method도 고려해볼 방안이다.
     *
     * @param uri getIntent().getData()
     * @see Extra#EX_URI_MSG
     * @see Extra#EX_URI_SCHEME_HOST
     */
    protected void checkFromURI(Uri uri) {
    }


    /*
        INFO: TTS, STT
     */
    private void initTTS() {
        new TTSHelper(this);
    }

    private void initSTT() {
        new STTHelper(this);
    }


    /*
        INFO: google login
     */
    private GoogleSignInHelper _googleSignInHelper;

    private void initGoogleSignIn() {
        _googleSignInHelper = new GoogleSignInHelper(this, this);
    }

    protected void checkGoogleSignIn() {
        if (_googleSignInHelper != null) {
            _googleSignInHelper.checkGoogleSignIn();
        }
    }

    @Override
    public void onCompleteCheckSignIn(GoogleSignInAccount account) {
        if (account != null) {
            setGoogleUserInfo(account);
        } else {
            /*
                TODO 로그인 만료시 어떤식으로 처리할지 적용 필요
             */
            if (this instanceof IntroActivity) {
                Log.i(TAG, "todo check login ...");
//                _shared.clearUserInfo();
            }
        }
    }

    /**
     * google login
     */
    protected void googleSignIn() {
        if (_googleSignInHelper != null) {
            _googleSignInHelper.signIn();
        }
    }

    @Override
    public void onCompleteSignIn(GoogleSignInAccount account, Status status) {
        if (account != null) {
            setGoogleUserInfo(account);
            requestRegist();
        } else {
            onLoginFinsih(false);
        }
    }

    /**
     * 구글 로그인된 사용자 정보 업데이트
     */
    protected void setGoogleUserInfo(GoogleSignInAccount account) {
        String idToken = account.getIdToken();
        String authCode = account.getServerAuthCode();
        String id = account.getId();
        String name = account.getDisplayName();
        String email = account.getEmail();

        _shared.setGoogleAccountIdToken(idToken);
        _shared.setGoogleAccountServerAuthCode(authCode);
        _shared.setGoogleAccountId(id);
        _shared.setGoogleAccountDisplayName(name);
        _shared.setGoogleAccountEmail(email);
        Uri photoUrl = account.getPhotoUrl();
        if (photoUrl != null) {
            _shared.setGoogleAccountPhotoUrl(photoUrl.toString());
        }
    }

    /**
     * google logout
     */
    public void googleSignOut() {
        if (_googleSignInHelper != null) {
            _googleSignInHelper.signOut();
        }
    }

    @Override
    public void onCompleteSignOut() {
        onSignOutFinishAndRestartApp();
    }


    /*
        INFO: login 처리
     */

    /**
     * 로그인 완료 처리
     */
    protected void onLoginFinsih(boolean isLogin) {
        Log.d(TAG, "onLoginFinsih | " + isLogin);
        if (!TextUtils.isEmpty(_shared.getAgreeAppArarmYN()) && Const.YES.equals(_shared.getAgreeAppArarmYN())) {
            MyFirebaseMessagingService.subscribeTopicOnlineNotice(true);
        }
    }

    /**
     * 로그아웃 완료 처리<br/>
     * 회원 정보 초기화 후 앱 재시작
     */
    public void onSignOutFinishAndRestartApp() {
        _shared.clearUserInfo();
        _shared.setLastLoginDialogViewTime(true);
        MyFirebaseMessagingService.subscribeTopicOnlineNotice(false);
        sendBroadcast(new Intent(ReceiverName.FINISH_EXCLUDE_MAIN));
        sendBroadcast(new Intent(ReceiverName.FINISH));
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setClass(_context, IntroActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    private LoginDialog _loginDialog = null;

    protected void showLoginDialog(boolean isVisibleCheckbox, boolean isImmediatelyLogin) {
        if (!isFinishing() && _loginDialog == null && _context != null) {
            _loginDialog = new LoginDialog(_context, isVisibleCheckbox, isImmediatelyLogin, new LoginDialog.OnClickListener() {
                @Override
                public void onCancle(boolean isCheck) {
                    if (isCheck) {
                        _shared.setLastLoginDialogViewTime(false);
                    }
                    onLoginDialogCancel();
                }

                @Override
                public void startLogin() {
                    googleSignIn();
                }
            });
            _loginDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    _loginDialog = null;
                }
            });
        }
        if (_loginDialog != null) {
            _loginDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            _loginDialog.show();
        }
    }

    /**
     * {{@link LoginDialog}} 취소버튼 클릭시 호출
     */
    protected void onLoginDialogCancel() {
    }

    protected void requestRegist() {
        requestLogin();
        if (true) return;
        /*
            TODO: 회원가입 구현
         */
        final Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                closeProgress();
                UserDto dto = (UserDto) msg.obj;
                if (dto.data != null && dto.data.id > 0) {
                    User user = dto.data;
                    _shared.setUserId(user.id);
                    requestLogin();
                } else {
                    /*
                        TODO: 회원가입 오류시 행동 구현
                     */
                    onLoginFinsih(false);
                }
                return false;
            }
        });
        showProgress();
        new AccountController(this, handler).register(_shared.getUserSsoSerial());
    }

    protected void requestLogin() {
        onLoginFinsih(true);
        if (true) return;
        /*
            TODO: 로그인 구현
         */
        final Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                closeProgress();
                UserDto dto = (UserDto) msg.obj;
                if (dto.data != null && !TextUtils.isEmpty(dto.data.session)) {
                    User user = dto.data;
                    _shared.setUserSession(user.session);
                    _shared.setUserId(user.id);
                    onLoginFinsih(true);
                } else {
                    /*
                        TODO: 로그인 오류시 행동 구현
                     */
                    onLoginFinsih(false);
                }
                return false;
            }
        });
        showProgress();
        new AccountController(this, handler).signin();
    }


    /*
        INFO: init base UI
     */
    private final int _baseLayoutResId = R.layout.ui_base_layout;
    //private int _baseLayoutResId = R.layout.ui_base_layout_drawer;
    private FrameLayout _uiBaseFlRoot;
    private DrawerLayout _uiBaseDlRoot;
    private ImageView _uiBaseIvBackground;
    private CoordinatorLayout _uiBaseClMain;
    protected AppBar _appBar;
    private SwipeRefreshLayout _uiBaseSrlWrap;
    protected CoordinatorLayout _uiBaseClBody;
    private CoordinatorLayout _uiBaseClFooter;
    private UiBaseLayoutBinding _baseLayoutBinding;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        if (layoutResID == _baseLayoutResId) {
//            _uiBaseFlRoot = findViewById(R.id.uiBaseFlRoot);
//            _uiBaseDlRoot = findViewById(R.id.uiBaseDlRoot);
            _uiBaseClMain = findViewById(R.id.uiBaseClMain);
            _appBar = findViewById(R.id.uiBaseAppbar);
            _uiBaseClBody = findViewById(R.id.uiBaseClBody);
            _uiBaseClFooter = findViewById(R.id.uiBaseClFooter);
            _uiBaseSrlWrap = findViewById(R.id.uiBaseSrlWrap);
            initSwipeRefreshLayout(_uiBaseSrlWrap);
            /*
                공통 배경 적용
             */
            _uiBaseIvBackground = findViewById(R.id.uiBaseIvBackground);
            if (this instanceof IntroActivity || this instanceof MainActivity) {
                _uiBaseIvBackground.setVisibility(View.VISIBLE);
            }
        }
        //initSlideArea();
    }

    public <T extends ViewDataBinding> T setContentViewBinding(int layoutResID) {
        T viewDataBinding = DataBindingUtil.setContentView(this, layoutResID);
        if (layoutResID == _baseLayoutResId) {
            _baseLayoutBinding = (UiBaseLayoutBinding) viewDataBinding;
            _uiBaseClMain = _baseLayoutBinding.uiBaseClMain;
            _appBar = _baseLayoutBinding.uiBaseAppbar;
            _uiBaseClBody = _baseLayoutBinding.uiBaseClBody;
            _uiBaseClFooter = _baseLayoutBinding.uiBaseClFooter;
            _uiBaseSrlWrap = _baseLayoutBinding.uiBaseSrlWrap;
            initSwipeRefreshLayout(_uiBaseSrlWrap);
        }
        return viewDataBinding;
    }

    /**
     * UI 새로고침
     */
    private void initSwipeRefreshLayout(SwipeRefreshLayout swipeRefreshLayout) {
        swipeRefreshLayout.setEnabled(false);
        if (this instanceof SwipeRefreshLayoutListener) {
            swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.progress_swipeRefreshLayout));
            swipeRefreshLayout.setEnabled(true);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refreshUI();
                    swipeRefreshLayout.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }, 500);
                }
            });
            final SwipeRefreshLayoutListener listener = (SwipeRefreshLayoutListener) this;
            listener.initRefreshUI(swipeRefreshLayout);
        }
    }

    @Nullable
    public AppBar getAppBar() {
        return _appBar;
    }

    protected int getBodyId() {
        return _uiBaseClBody != null ? _uiBaseClBody.getId() : android.R.id.content;
    }

    protected SwipeRefreshLayout getSwipeRefreshLayout() {
        return _uiBaseSrlWrap;
    }

    public interface SwipeRefreshLayoutListener {
        void initRefreshUI(SwipeRefreshLayout srlWrap);
    }

    /**
     * UI : set body view<p>
     * {@link #onCreate(Bundle)}에서 사용
     *
     * @param layoutId layoutId에 해당하는 view를 {@link #_uiBaseClBody}에 add
     */
    protected void setBodyView(int layoutId) {
        View view = LayoutInflater.from(this).inflate(layoutId, _uiBaseClBody, false);
        setBodyView(view);
    }

    protected <T extends ViewDataBinding> T setBodyViewDataBinding(int layoutId) {
        T binding = DataBindingUtil.inflate(getLayoutInflater(), layoutId, null, false);
        setBodyView(binding.getRoot());
        return binding;
    }

    /**
     * @see #setBodyView(int)
     */
    protected void setBodyView(View view) {
        //initSlideArea();
        if (_uiBaseClBody != null && view != null) {
            view.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
            _uiBaseClBody.removeAllViews();
            _uiBaseClBody.addView(view, 0);
        } else {
            removeFooter();
        }
    }

    protected void setBodyView(FragmentEx fragment) {
        setBodyView(fragment, false, null);
    }

    protected void setBodyView(FragmentEx fragment, boolean useBackStack, String backStackName) {
        //initSlideArea();
        fragmentTransactionCommit(getBodyId(), fragment, fragment.TAG, useBackStack, backStackName);
    }

    protected LinearLayout _bodyView;

    protected void setBaseBodyView() {
        NestedScrollView sv = new NestedScrollView(this);
        sv.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        sv.setFillViewport(true);

        LinearLayout child = new LinearLayout(this);
        child.setOrientation(LinearLayout.VERTICAL);
        child.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
        ViewUtil.setPadding(_context, child, 10, 10, 10, 10);

        sv.addView(child);
        setBodyView(sv);
        _bodyView = child;
    }

    protected final LinearLayout setEmptyContentView() {
        NestedScrollView sv = new NestedScrollView(this);
        sv.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        sv.setFillViewport(true);

        LinearLayout child = new LinearLayout(this);
        child.setOrientation(LinearLayout.VERTICAL);
        child.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
        ViewUtil.setPadding(_context, child, 10, 10, 10, 10);

        sv.addView(child);
        super.setContentView(sv);
        return child;
    }

    protected final Button Button(ViewGroup parent, String btName) {
        Button bt = new Button(this);
        bt.setText(btName);
        bt.setPadding(0, 0, 0, 0);
        bt.setIncludeFontPadding(false);
        bt.setLayoutParams(ViewUtil.getMarginLayoutParams(_context, new LinearLayout.LayoutParams(-1, -2), 5, 5, 5, 5));

        if (parent != null) parent.addView(bt);
        return bt;
    }

    protected final TextView TextView(ViewGroup parent, String contents) {
        TextView tv = new TextView(this);
        tv.setText(contents);
        tv.setTextColor(Color.BLACK);
        tv.setPadding(5, 0, 5, 0);
        tv.setLayoutParams(ViewUtil.getMarginLayoutParams(_context, new LinearLayout.LayoutParams(-1, -2), 0, 5, 0, 0));

        if (parent != null) parent.addView(tv);
        return tv;
    }

    protected final EditText EditText(ViewGroup parent, String hint) {
        EditText et = new EditText(this);
        et.setHint(hint);
        et.setPadding(10, 10, 10, 10);
        et.setLayoutParams(ViewUtil.getMarginLayoutParams(_context, new LinearLayout.LayoutParams(-1, -2), 0, 0, 0, 0));

        if (parent != null) parent.addView(et);
        return et;
    }

    protected final void makeIntentView(View view, final Class<?> cls) {
        OnClickListener listener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(_appContext, cls));
            }
        };
        view.setOnClickListener(listener);
    }

    @Override
    protected void initUI() {
        if (_appBar != null) {
            initTitle(_appBar);
        } else {
            initTitle();
        }
        initFooter();
        initBody();
    }

    /**
     * {@link #initUI()}에서 호출되며 UI의 title 영역 구현<br/>
     * {@link #_appBar} 사용
     */
    protected abstract void initTitle(AppBar appBar);

    @Override
    @Deprecated
    protected void initTitle() {
        // no-op
    }

    /**
     * {@link #initUI()}에서 호출되며 UI의 footer 영역 구현<br/>
     * {@link #setFooter(int)} 사용
     */
    protected abstract void initFooter();

    /**
     * {@link #initUI()}에서 호출되며 UI의 body 영역 구현<br/>
     */
    protected abstract void initBody();

    /**
     * set footer<br>
     *
     * @param layoutId layoutId에 해당하는 view를 {@link #_uiBaseClFooter}에 add
     */
    protected void setFooter(int layoutId) {
        View footer = LayoutInflater.from(this).inflate(layoutId, _uiBaseClFooter, false);
        this.setFooter(footer);
    }

    /**
     * @see #setFooter(int)
     */
    protected void setFooter(View footer) {
        if (_uiBaseClFooter != null && footer != null) {
            _uiBaseClFooter.removeAllViews();
            _uiBaseClFooter.addView(footer);
            footer.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            _uiBaseClFooter.setVisibility(View.VISIBLE);
        } else {
            removeFooter();
        }
    }

    /**
     * remove {@link #_uiBaseClFooter} child view
     */
    protected void removeFooter() {
        if (_uiBaseClFooter != null && _uiBaseClFooter.getChildCount() > 0) {
            _uiBaseClFooter.setVisibility(View.GONE);
            _uiBaseClFooter.removeAllViews();
        }
    }

    private SlideMenuLayout _llMenu;
    private boolean _isSlideMenuUse;

    /**
     * init slide : DrawerLayout
     */
    protected void initSlideArea() {
        if (_uiBaseDlRoot == null) {
            return;
        } else if (!(this instanceof AppCompatActivityEx)) {
            _uiBaseDlRoot.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            return;
        }
        _uiBaseDlRoot.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        _uiBaseDlRoot.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        /*
        _isSlideMenuUse = true;
        _llMenu = (SlideMenuLayout) findViewById(R.id.vSlideMenu);
        _llMenu.setDrawerLayout(_uiBaseDlRoot);*/
    }

    @Override
    public void onSlideMenuClick() {
        if (_llMenu != null && _uiBaseDlRoot != null) {
            _uiBaseDlRoot.openDrawer(_llMenu);
        }
    }

    /**
     * 화면 안내 문구 노출
     *
     * @param span TextView 에 삽입될 SpannableString
     */
    protected void showScreenGuide(SpannableString span) {
        if (_shared.isShownScreenGuide(TAG)) {
            final int margin = (int) ViewUtil.dp2px(2, _context);

            final TextView tv = new TextView(_context);
            tv.setBackgroundResource(R.drawable.bt_round_white);
            tv.setIncludeFontPadding(false);
            tv.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
            tv.setPadding(margin * 12, margin * 5, margin * 12, margin * 5);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            tv.setTextColor(ContextCompat.getColor(_context, R.color.black));
            tv.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            tv.setGravity(Gravity.CENTER);

            /*String msg = getString(R.string.);
            SpannableString span = new SpannableString(msg);
            span.setSpan(new ForegroundColorSpan(ContextCompat.getColor(_context, R.color.color_first)), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);*/
            tv.setText(span);

            Toast t = new Toast(_context);
            t.setView(tv);
            t.setDuration(Toast.LENGTH_LONG);
            t.setGravity(Gravity.TOP, 0, margin * 8);
            t.show();
        }
    }


    /*
        INFO: progress dialog
     */
    private Dialog _pDialog;
    private int DIALOG_TYPE = 1;

    @Override
    public Dialog showProgress() {
        if (_pDialog == null) {
            if (this instanceof IntroActivity) {
                _pDialog = getProgressIntro();
            } else {
                switch (DIALOG_TYPE) {
                    case 1:
                    default:
                        _pDialog = getProgress1();
                        break;
                    case 2:
                        _pDialog = getProgress2();
                        break;
                }
            }
        }
        if (!isFinishing() && !_pDialog.isShowing()) {
            try {
                _pDialog.show();
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        return _pDialog;
    }

    @Override
    public Dialog showProgress(View view, Drawable backGround) {
        if (_pDialog == null) {
            DialogView dv;
            if (view != null) {
                dv = new DialogView(this, view, backGround);
            } else {
                dv = new DialogView(this, DialogView.Size.small);
            }
            _pDialog = dv.getDialog(false, true);
            _pDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
        if (!isFinishing() && !_pDialog.isShowing()) {
            try {
                _pDialog.show();
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        return _pDialog;
    }

    private LottieAnimationView getLottieView() {
        int size = (int) ViewUtil.dp2px(90, _context);
        LottieAnimationView view = new LottieAnimationView(_context);
        view.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
        view.setAnimation("loading-gears1.json");
        view.setRepeatMode(LottieDrawable.RESTART);
        view.setRepeatCount(LottieDrawable.INFINITE);
        view.playAnimation();
        return view;
    }

    private Dialog getProgressIntro() {
        ImageView view = new ImageView(this);
        view.setImageResource(R.drawable.ic_emoticon);
        int size = (int) ViewUtil.dp2px(100, _context);
        view.setLayoutParams(new ViewGroup.LayoutParams(size, size));
        view.setScaleType(ImageView.ScaleType.FIT_CENTER);
        DialogView dv = new DialogView(this, view, null);
        Dialog dialog = dv.getDialog(false, true);
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        return dialog;
    }

    private Dialog getProgress1() {
        View view = getLottieView();
        DialogView dv = new DialogView(this, view, null);
        Dialog dialog = dv.getDialog(false, true);
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        return dialog;
    }

    private Dialog getProgress2() {
        ProgressBar progressBar = new ProgressBar(_context, null, DialogView.Size.medium.defStyle);
        progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(_context, R.color.progress_swipeRefreshLayout), PorterDuff.Mode.MULTIPLY); //PorterDuff.Mode.SRC_IN
        DialogView dv = new DialogView(_context, progressBar, null);
//        DialogView dv = new DialogView(_context, new ProgressBar(_context, null, DialogView.Size.medium.defStyle), null);
//        DialogView dv = new DialogView(_context, DialogView.Size.small);
        Dialog dialog = dv.getDialog(false, false);
        return dialog;
    }

    @Override
    public void closeProgress() {
        super.closeProgress();
        if (_pDialog != null && _pDialog.isShowing()) {
            _pDialog.dismiss();
            _pDialog = null;
        }
    }

    /*
        INFO etc
     */
    private void initBlurBackground(final ImageView imageView) {
        /*
            TODO: 2020-02-06 M(23) 이하 버전에서 android.permission.WRITE_SETTINGS permission 오류 발생 수정 필요
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (WallpaperManager.getInstance(_context).isWallpaperSupported()) {
            }
        }
        if (!isFinishing() && imageView != null && imageView.getVisibility() == View.VISIBLE) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Drawable drawable = WallpaperManager.getInstance(_context).getDrawable();
            if (imageView.getVisibility() == View.VISIBLE) {
                PorterDuffColorFilter filter = new PorterDuffColorFilter(ContextCompat.getColor(this, R.color.bg_box01), PorterDuff.Mode.SRC_ATOP);
                drawable.setColorFilter(filter);
                imageView.setImageDrawable(drawable);
                try {
                    imageView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (!isFinishing()) {
                                    /*
                                        Blurry is an easy blur library for Android. https://github.com/wasabeef/Blurry
                                        implementation 'jp.wasabeef:blurry:3.0.0'
                                     */
                                    /*Blurry.with(_context).radius(25).sampling(2)
                                            //.color(ContextCompat.getColor(this, R.color.blurImageFilter))
                                            //.color(Color.argb(200, 0, 0, 0))
                                            .async()
                                            //.animate(300)
                                            .capture(imageView)
                                            .into(imageView);*/
                                }
                            } catch (Exception e) {
                                Log.e(true, TAG, e.getMessage(), e);
                            }
                        }
                    }, 0);
                } catch (Exception e) {
                    Log.e(true, TAG, e.getMessage(), e);
                }
            }
        }
    }


    /*
        INFO: intent
     */
    public void fragmentTransactionCommit(int containerViewId, Fragment fragment, String tag, boolean useBackStack, String backStackName) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(containerViewId, fragment, tag);
        if (useBackStack) {
            fragmentTransaction.addToBackStack(backStackName);
        }
        fragmentTransaction.commit();
    }


    /*
        INFO: activity callback
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*switch (requestCode) {
            default:
                break;
        }*/
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        if (intent.getComponent() != null) {
            sendBroadcast(new Intent(intent.getComponent().getClassName()));
        }
        if (true) return;
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }


    /*
        INFO: finish animation
     */
    @Override
    public void finishAfterTransition() {
        super.finishAfterTransition();
        if (true) return;
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void finish() {
        super.finish();
        closeProgress();
        if (true) return;
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

}