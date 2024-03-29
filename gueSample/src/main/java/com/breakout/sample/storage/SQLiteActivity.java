package com.breakout.sample.storage;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.breakout.sample.BaseActivity;
import com.breakout.sample.Log;
import com.breakout.sample.R;
import com.breakout.sample.constant.ReceiverName;
import com.breakout.sample.storage.data.TrainTimeTable;
import com.breakout.sample.storage.data.User;
import com.breakout.sample.views.AppBar;
import com.breakout.util.res.AnimationSuite;
import com.breakout.util.string.StringUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class SQLiteActivity extends BaseActivity {

    private LinearLayout _llInput;
    private EditText _etNick;
    private EditText _etEmail;
    private RadioGroup _rgGender;
    private EditText _etBirth;

    private LinearLayout _llResult;
    private EditText _etDel;
    private ListView _lv;

    private DbHelper _dbHelper;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.registerfinishReceiver(ReceiverName.FINISH_EXCLUDE_MAIN);
        super.setContentView(R.layout.ui_base_layout);
        super.setBodyView(R.layout.sqlite_layout);

        _dbHelper = new DbHelper(getApplicationContext());

        super.initUI();
    }

    private void testLocalDB() {
        try {
            LocalDBHelper localDBHelper = new LocalDBHelper(_context);
            String upStation = localDBHelper.getUpStation();
            String downStation = localDBHelper.getDownStation();
            ArrayList<TrainTimeTable> listFirstAndLast = localDBHelper.getListFirstAndLast("평일");
            ArrayList<TrainTimeTable> list = localDBHelper.getList(TrainTimeTable.Type.WEEKDAY_UP);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    @Override
    protected void initTitle(AppBar appBar) {
        appBar.setVisibility(View.VISIBLE);
        appBar.fixAppBarLocation(false)
                .setHomeIcon(true)
                .setTitle("SQLite3")
        ;
    }

    @Override
    protected void initFooter() {
    }

    @Override
    protected void initBody() {
        _llInput = (LinearLayout) findViewById(R.id.llInput);
        _etNick = (EditText) findViewById(R.id.etNick);
        _etEmail = (EditText) findViewById(R.id.etEmail);
        _rgGender = (RadioGroup) findViewById(R.id.rgGender);
        _etBirth = (EditText) findViewById(R.id.etBirth);

        _llResult = (LinearLayout) findViewById(R.id.llResult);
        _etDel = (EditText) findViewById(R.id.etDel);
        _lv = (ListView) findViewById(R.id.lv);

        _etBirth.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                final Calendar c = Calendar.getInstance();
                if (_etBirth.getText().toString().length() > 0) {
                    Date d;
                    try {
                        d = formater.parse(_etBirth.getText().toString());
                        c.setTime(d);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                OnDateSetListener listener = new OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        c.set(Calendar.YEAR, year);
                        c.set(Calendar.MONTH, monthOfYear);
                        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        _etBirth.setText(formater.format(c.getTime()));
                    }
                };
                new DatePickerDialog(
                        _context,
                        listener,
                        c.get(Calendar.YEAR),
                        c.get(Calendar.MONTH),
                        c.get(Calendar.DAY_OF_MONTH)
                ).show();
            }
        });

        findViewById(R.id.btOk).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String nick = _etNick.getText().toString();
                String email = _etEmail.getText().toString();
                String gender = null;
                switch (_rgGender.getCheckedRadioButtonId()) {
                    case R.id.rbFemale:
                        gender = "female";
                        break;
                    case R.id.rbMale:
                        gender = "male";
                        break;
                }
                String birth = _etBirth.getText().toString();

                if (!TextUtils.isEmpty(nick) && !TextUtils.isEmpty(email) &&
                    !TextUtils.isEmpty(gender) && !TextUtils.isEmpty(birth)) {
                    int userId = _dbHelper.getLastUserId();
                    ContentValues values = new ContentValues();
                    values.put(User.Entry.nick, nick);
                    values.put(User.Entry.email, email);
                    values.put(User.Entry.gender, gender);
                    values.put(User.Entry.birth, birth);
                    long result = _dbHelper.insertUser(values);
                    String str = userId + " / " + nick + " / " + email + " / " + gender + " / " + birth + " / 결과 : " + result;
                    Toast.makeText(_context, str, Toast.LENGTH_SHORT).show();
                    showPop();
                } else {
                    Toast.makeText(_appContext, "내용 다 채워!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.btList).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showPop();
            }
        });

        findViewById(R.id.btDrop).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                _dbHelper.delUserInfo(null);
            }
        });

        findViewById(R.id.btDel).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = _etDel.getText().toString();
                _etDel.setText(null);
                if (StringUtil.nullCheck(userId) != null) {
                    _dbHelper.delUserInfo(userId);
                    showPop();
                }
            }
        });
    }

    @Override
    protected void refreshUI() {
    }


    @SuppressWarnings("deprecation")
    private final void showPop() {
        _llInput.setVisibility(View.GONE);
        _llResult.setVisibility(View.VISIBLE);
        _llInput.setAnimation(AnimationSuite.hold());
        _llResult.setAnimation(AnimationSuite.zoomIn());

        cursorClose();
        cursor = _dbHelper.getUserList();

        CursorAdapter cursorAdapter = new CursorAdapter(_appContext, cursor) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                LayoutInflater inflater = LayoutInflater.from(context);
                View view = inflater.inflate(R.layout.sqlite_row, parent, false);
                return view;
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                ((TextView) ((ViewGroup) view).getChildAt(0)).setText(cursor.getString(cursor.getColumnIndexOrThrow(User.Entry.userId)));
                ((TextView) ((ViewGroup) view).getChildAt(1)).setText(cursor.getString(cursor.getColumnIndexOrThrow(User.Entry.nick)));
                ((TextView) ((ViewGroup) view).getChildAt(2)).setText(cursor.getString(cursor.getColumnIndexOrThrow(User.Entry.gender)));
                ((TextView) ((ViewGroup) view).getChildAt(3)).setText(cursor.getString(cursor.getColumnIndexOrThrow(User.Entry.birth)));
                ((TextView) ((ViewGroup) view).getChildAt(4)).setText(cursor.getString(cursor.getColumnIndexOrThrow(User.Entry.email)));
            }
        };

        _lv.setAdapter(cursorAdapter);
        _lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cursor.moveToPosition(position);
                String userId = cursor.getString(cursor.getColumnIndexOrThrow("user_id"));
                _etDel.setText(userId);
            }
        });
    }


    private void cursorClose() {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
            cursor = null;
        }
    }

    @Override
    public void onBackPressed() {
        if (_llResult.isShown()) {
            _llInput.setVisibility(View.VISIBLE);
            _llResult.setVisibility(View.GONE);
            _llInput.setAnimation(AnimationSuite.hold());
            _llResult.setAnimation(AnimationSuite.zoomOut());
        } else super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        cursorClose();
        _dbHelper.close();
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
    }


}
