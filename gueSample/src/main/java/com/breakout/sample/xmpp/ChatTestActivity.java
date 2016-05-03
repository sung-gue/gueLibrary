package com.breakout.sample.xmpp;

import java.util.ArrayList;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;

import com.breakout.sample.BaseActivity;
import com.breakout.sample.R;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.breakout.util.Log;
import com.breakout.util.string.StringUtil;

public class ChatTestActivity extends BaseActivity implements OnClickListener, Chat.OnChatListener {
	
	public final String HOST = "tilcchat.ilikecamping.co.kr";
	public final int PORT = 5222;
	public final String SERVICE = null;

	private ArrayList<String> _msgList;
	private ArrayAdapter<String> _chatAdapter;

	private TextView _tvTitle;
	private LinearLayout _llInitArea;
	private EditText _etUserName;
	private EditText _etUserPw;
	private EditText _etTargetUser;
	private EditText _etTargetRoom;
	private LinearLayout _llChatArea;
	private ListView _lvMsg;
	private EditText _etChatMsg;

	private String _userName;
	private String _userPw;
	private String _targetUser;
	private String _targetRoom;
	
	private boolean _isJoin;
	private boolean _isRoomChat;
	
	private Chat _chat;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xmpp_chat_main);
		
		try {
			_chat = Chat.getInstance();
			_chat.init(HOST, PORT, SERVICE, this, getApplicationContext());
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}
		
		super.initUI();
	}

	@Override
	protected void initTitle() {}

	@Override
	protected void initFooter() {}

	@Override
	protected void initBody() {
		_tvTitle = (TextView) findViewById(R.id.tvTitle);
		
		_llInitArea = (LinearLayout) findViewById(R.id.llInitArea);
		_etUserName = (EditText) findViewById(R.id.etUserName);
		_etUserPw= (EditText) findViewById(R.id.etUserPw);
		_etTargetUser = (EditText) findViewById(R.id.etTargetUser);
		_etTargetRoom = (EditText) findViewById(R.id.etTargetRoom);
		_etChatMsg = (EditText) findViewById(R.id.etChatMsg);
		
		_llChatArea = (LinearLayout) findViewById(R.id.llChatArea);
		_lvMsg = (ListView) findViewById(R.id.lvMsg);
		
		setMainArea(false);
		
		findViewById(R.id.btJoin).setOnClickListener(this);
		findViewById(R.id.btSend).setOnClickListener(this);
		
		//test3 / 120000000007 , test4 / 120000000008, iostest1003 / 140000000556
		_etUserName.setText("test3");
		_etUserPw.setText("120000000007");
		_etTargetUser.setText("test");
//		_etTargetRoom.setText("120000000007_1408945964461");
		
//		_etUserName.setText("iostest1");
//		_etUserPw.setText("130000000078");
//		_etTargetRoom.setText("1_130000000078_140000000556");
	}

	@Override
	protected void refreshUI() {
		setMainArea(false);
	}
	
	private void setMainArea(boolean isJoin) {
		_isJoin = isJoin;
		if (_isJoin) {
			String title = "target ";
			if (_isRoomChat) title += "room : " + _targetRoom + "@" + HOST;
			else title += "user : " + _targetUser + "@" + HOST;
			_tvTitle.setText(title);
			_llInitArea.setVisibility(View.GONE);
			_llChatArea.setVisibility(View.VISIBLE);
		}
		else {
			_msgList = new ArrayList<String>();
			_chatAdapter = null;
			_userName = null;
			_userPw = null;
			_targetUser = null;
			_targetRoom = null;
			setChatAdapter();
			_tvTitle.setText("server : " + HOST + ":" + PORT);
			_llInitArea.setVisibility(View.VISIBLE);
			_llChatArea.setVisibility(View.GONE);
		}
	}
	
	private void setChatAdapter() {
		if (_chatAdapter == null) {
			_chatAdapter = new ArrayAdapter<String>(this, R.layout.xmpp_chat_row, _msgList);
			_lvMsg.setAdapter(_chatAdapter);
		}
		else {
			_chatAdapter.notifyDataSetChanged();
		}
	}
	
	private void enterRoom() {
		if (_isRoomChat) {
			try {
				_chat.createOrJoinRoom(_targetRoom, null);
			} catch (NoResponseException e) {
				e.printStackTrace();
			} catch (XMPPErrorException e) {
				e.printStackTrace();
			} catch (SmackException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void onChatCompleteLogin(boolean isSuccess) {
		closeProgress();
		if (isSuccess) {
			setMainArea(true);
			if (_isRoomChat) enterRoom();
		}
		else {
			Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	public void onChatSendMessage(boolean isSuccess, String to, String msg) {
		if (isSuccess) {
			_msgList.add(_userName + ":\n" + msg);
			setChatAdapter();
			_lvMsg.setSelection(_lvMsg.getCount());
		}
	}
	
	@Override
	public void onChatReceiveMessage(String from, String msg) {
		if (StringUtil.nullCheckB(msg)) {
			_msgList.add(from + ":\n" + msg);
			setChatAdapter();
		}
	}
	
	@Override
	public void onChatSendRoomMessage(boolean isSuccess, String room, String msg) {
		if (isSuccess) {
//			_msgList.add(_userName + ":\n" + msg);
//			setChatAdapter();
			_lvMsg.setSelection(_lvMsg.getCount());
		}
	}
	
	@Override
	public void onChatReceiveRoomMessage(String roomName, String from, String msg) {
		if (StringUtil.nullCheckB(msg)) {
			_msgList.add(from + ":\n" + msg);
			setChatAdapter();
		}
	}
	
	@Override
	public void onChatConnectionAuthenticated() {
	}
	
	@Override
	public void onChatConnectionClosed(boolean isSuccess, Exception e) {
	}
	
	@Override
	public void onChatReconnection(boolean isSuccess, Exception e) {
		if (isSuccess) {
			if (_isRoomChat) enterRoom();
		}
	}
	
	
	
/* ************************************************************************************************
 * INFO listener
 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btJoin:{
				String userName = _etUserName.getText().toString().trim();
				String userPw = _etUserPw.getText().toString().trim();
				String targetUser = _etTargetUser.getText().toString().trim();
				String targetRoom = _etTargetRoom.getText().toString().trim();
				
				if (StringUtil.nullCheckB(userName) && StringUtil.nullCheckB(userPw) && (StringUtil.nullCheckB(targetUser) || StringUtil.nullCheckB(targetRoom)) ) {
					_userName = userName;
					_userPw = userPw;
					if (StringUtil.nullCheckB(targetUser)) {
						_targetUser = targetUser;
						_targetRoom = null;
						_isRoomChat = false;
					}
					else {
						_targetUser = null;
						_targetRoom = targetRoom;
//						_targetRoom = "120000000007_" + new Date().getTime();
						_isRoomChat = true;
					}
					showProgress();
					_chat.connect(_userName, _userPw);
//					_chat.connect(_userName + "@" + HOST, _userPw);
					
				}
				else Toast.makeText(this, "fill in the blanks", Toast.LENGTH_SHORT).show();
				break;
			}
			case R.id.btSend:{
				String chatMsg = _etChatMsg.getText().toString().trim();
				if (StringUtil.nullCheckB(chatMsg)) {
					String targetTo = null;
					if (_isRoomChat) {
						targetTo = _targetRoom;
						_chat.sendRoomMsg(chatMsg);
					}
					else {
						targetTo = _targetUser;
						_chat.sendMsg(targetTo, chatMsg);
					}
				}
				break;
			}
			default:
				break;
		}
		
	}


/* ************************************************************************************************
 * INFO life cycle
 */
	@Override
	protected void onDestroy() {
		if (_chat != null) _chat.disconnect();
		super.onDestroy();
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
	
}



