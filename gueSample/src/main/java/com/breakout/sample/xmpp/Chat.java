package com.breakout.sample.xmpp;

import android.content.Context;
import android.os.Handler;

import com.breakout.util.Log;
import com.breakout.util.string.StringUtil;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.SmackAndroid;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo;
import org.jivesoftware.smackx.disco.packet.DiscoverItems;
import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.RoomInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * XMPP Chat Util, use aSmack <br/>
 * use
 * <pre>
 * Chat.getInstace().init();
 * </pre>
 *
 * @author sung-gue
 * @version 1.0 (2014. 8. 12.)
 */
public final class Chat {
    private final boolean DEBUG = true;
    private final String TAG = getClass().getSimpleName();

    private String HOST;
    private int PORT;
    private String SERVICE;// = "";

    private XMPPConnection _connection;
    private SmackAndroid _smackAndroid;
    private Handler _handler = new Handler();
    private MultiUserChat _muc;
    private PacketListener _customPacketListener;
    private PacketListener _customMucPacketListener;

    private OnChatListener _listener;
    private Context _context;

    private String _userName;
    private String _userPw;
    private String _joinRoomName;
    private String _joinRoomNamePw;

    private static Chat _this;

    private enum ConnectType {
        CONNECT,
        LOGIN,
    }

    private Chat() {
    }

    public final static synchronized Chat getInstance() {
        if (_this == null) {
            _this = new Chat();
        }
        return _this;
    }

    /**
     * {@link MultiUserChat#leave()}, {@link XMPPConnection#disconnect()}, {@link SmackAndroid#onDestroy()}, instance 해제까지 총 4가지 과정을 수행
     */
    public final static synchronized void destroyInstance() {
        if (_this != null) {
            _this.onDestroy();
        }
    }

    private void onDestroy() {
        try {
            _this.disconnect();
            if (_smackAndroid != null) _smackAndroid.onDestroy();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    /**
     * {@link OnChatListener}를 제거하고 connection을 해제
     */
    public void disconnect() {
        try {
            _listener = null;
            if (_muc != null) _muc.leave();
            if (_connection != null) _connection.disconnect();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }


    /**
     * @param context {@link Context#getApplicationContext()}
     * @throws Exception host or listener or context params is null
     */
    public void init(String host, int port, String service, OnChatListener listener, Context context) throws Exception {
        if (!StringUtil.nullCheckB(host) || listener == null || context == null) {
            throw new Exception("host or context params is null");
        }
        HOST = host;
        PORT = port;
        SERVICE = service;
        _listener = listener;
        _context = context;
        if (_smackAndroid == null) _smackAndroid = SmackAndroid.init(_context);
    }

    /**
     * {@link OnChatListener#onChatCompleteLogin(boolean)} 으로 성공 여부 전달
     */
    public void connect(final String userName, final String userPw) {
        // java.net.SocketException: Bad address family
//        System.setProperty("java.net.preferIPV6Addresses", "false");

        new Thread(new Runnable() {
            @Override
            public void run() {
                ConnectionConfiguration connConfig = new ConnectionConfiguration(HOST, PORT, SERVICE);
                // auto reconnect
                connConfig.setReconnectionAllowed(true);
                // off sucurity connect (SSL off)
                connConfig.setSecurityMode(SecurityMode.disabled);
                _connection = new XMPPTCPConnection(connConfig);
                addDefaultPacketListener();
                try {
                    _connection.connect();
                    try {
//                        SASLAuthentication.supportSASLMechanism("PLAIN", 0);
                        _connection.login(userName, userPw);
                        _userName = userName;
                        _userPw = userPw;
                        Log.i(TAG, "login success : " + _connection.getUser());
                        logXMPPConnection(_connection);

                        // send presence
                        Presence presence = new Presence(Presence.Type.available);
                        _connection.sendPacket(presence);

                        // end login
                        finishLogin(true, ConnectType.LOGIN, null);

                        // log roster entry
//                        logRoster(_connection.getRoster());
//                        getHosedRooms();
                    } catch (Exception e) {
                        finishLogin(false, ConnectType.LOGIN, e);
                    }
                } catch (Exception e) {
                    finishLogin(false, ConnectType.CONNECT, e);
                }
            }
        }).start();
    }

    private void finishLogin(final boolean isSuccess, ConnectType type, Exception e) {
        if (!isSuccess && type != null) {
            switch (type) {
                case CONNECT:
                    Log.e(TAG, "connect fail : " + e.getMessage(), e);
                    if (e instanceof SmackException) {

                    } else if (e instanceof IOException) {

                    } else if (e instanceof XMPPException) {

                    } else if (e instanceof Exception) {

                    }
                    break;
                case LOGIN:
                    Log.e(TAG, "login fail : " + e.getMessage(), e);
                    if (e instanceof SmackException) {

                    } else if (e instanceof IOException) {

                    } else if (e instanceof XMPPException) {

                    } else if (e instanceof Exception) {

                    }
                    break;
                default:
                    break;
            }
        }
        _handler.post(new Runnable() {
            @Override
            public void run() {
                if (_listener != null) _listener.onChatCompleteLogin(isSuccess);
            }
        });
    }

    private PacketListener _defaultPacketListener = new PacketListener() {
        @Override
        public void processPacket(Packet packet) {
            if (packet instanceof Message) {
                Message message = (Message) packet;
                if (message.getBody() != null) {
                    final String from = StringUtils.parseBareAddress(message.getFrom());
                    final String msg = message.getBody();
                    Log.d(TAG, "[XMPPconnection PacketListener.processPacket()] Receive msg : " + msg + " / from : " + from);
                    _handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (_listener != null) _listener.onChatReceiveMessage(from, msg);
                        }
                    });
                }
            }
        }
    };
    private ConnectionListener _connectionListener = new ConnectionListener() {
        @Override
        public void reconnectionSuccessful() {
            Log.w(TAG, "[XMPP ConnectionListener] reconnectionSuccessful()");
            if (_listener != null) _listener.onChatReconnection(true, null);
        }

        @Override
        public void reconnectionFailed(Exception e) {
            Log.w(TAG, "[XMPP ConnectionListener] reconnectionFailed() : " + e.getMessage(), e);
            if (_listener != null) _listener.onChatReconnection(false, e);
        }

        @Override
        public void reconnectingIn(int seconds) {
            Log.w(TAG, "[XMPP ConnectionListener] reconnectingIn() : " + seconds);
        }

        @Override
        public void connectionClosedOnError(Exception e) {
            Log.w(TAG, "[XMPP ConnectionListener] connectionClosedOnError() : " + e.getMessage(), e);
            if (_listener != null) _listener.onChatConnectionClosed(false, e);
        }

        @Override
        public void connectionClosed() {
            Log.w(TAG, "[XMPP ConnectionListener] connectionClosed()");
            if (_listener != null) _listener.onChatConnectionClosed(true, null);
        }

        @Override
        public void connected(XMPPConnection connection) {
            Log.w(TAG, "[XMPP ConnectionListener] connected() : " + connection);
        }

        @Override
        public void authenticated(XMPPConnection connection) {
            Log.w(TAG, "[XMPP ConnectionListener] authenticated()");
            if (_listener != null) _listener.onChatConnectionAuthenticated();
        }
    };
    private ConnectionCreationListener _connectionCreationListener = new ConnectionCreationListener() {
        @Override
        public void connectionCreated(XMPPConnection connection) {
            Log.w(TAG, "[XMPP ConnectionCreationListener] ConnectionCreationListener() : " + connection);
        }
    };

    private void addDefaultPacketListener() {
        if (_connection != null) {
            XMPPConnection.addConnectionCreationListener(_connectionCreationListener);
            _connection.addConnectionListener(_connectionListener);
            /* add PacketListener
             * implements PacketFilter
             * AndFilter, FromMatchesFilter, IQReplyFilter, IQTypeFilter, MessageTypeFilter, NotFilter, OrFilter, PacketExtensionFilter, PacketIDFilter, PacketTypeFilter, ThreadFilter
             * ?gue? 필터 적용 관련 추가 필요함
             */
            PacketFilter chatFilter = new AndFilter(new MessageTypeFilter(Message.Type.chat));//, new PacketExtensionFilter("x", "jabber:x:conference"));
            _connection.addPacketListener(_defaultPacketListener, chatFilter);

            // add RosterListener
            /*Roster roster = _connection.getRoster();
            roster.addRosterListener(new RosterListener() {
                @Override
                public void presenceChanged(Presence presence) {
                    Log.i(TAG, "RosterListener.presenceChanged() : " + presence);
                }
                @Override
                public void entriesUpdated(Collection<String> addresses) {
                    Log.i(TAG, "RosterListener.entriesUpdated() : " + addresses);
                }
                @Override
                public void entriesDeleted(Collection<String> addresses) {
                    Log.i(TAG, "RosterListener.entriesDeleted() : " + addresses);
                }
                @Override
                public void entriesAdded(Collection<String> addresses) {
                    Log.i(TAG, "RosterListener.entriesAdded() : " + addresses);
                }
            });*/
        }
    }

    public void addPacketListener(PacketListener listener, PacketFilter filter) {
        if (_connection != null) {
            _customPacketListener = listener;
            _connection.addPacketListener(listener, filter);
        }
    }
    
/*    private void removePacketListener() {
        if (_connection != null) {
            XMPPConnection.removeConnectionCreationListener(_connectionCreationListener);
            _connection.removeConnectionListener(_connectionListener);
            _connection.removePacketListener(_defaultPacketListener);
            if (_customPacketListener != null ) _connection.removePacketListener(_customMucPacketListener);
        }
    }*/

    public void sendMsg(String to, String msg) {
        boolean isSuccess = false;
        if (_connection != null) {
            String targetTo = to + "@" + HOST;
            Message message = new Message(targetTo, Message.Type.chat);
            message.setBody(msg);
            try {
                _connection.sendPacket(message);
                Log.i(TAG, "send msg : " + msg + " / to : " + targetTo);
                isSuccess = true;
            } catch (Exception e) {
                Log.e(TAG, "fail send msg : " + msg + " / " + e.getMessage(), e);
            }
        }
        if (_listener != null) _listener.onChatSendMessage(isSuccess, to, msg);
    }

    private String getRoomJid(String roomName) {
        return roomName + "@conference." + HOST;
    }

    private PacketListener _defaultMucPacketListener = new PacketListener() {
        @Override
        public void processPacket(Packet packet) throws NotConnectedException {
            if (packet instanceof Message) {
                if (_muc != null && packet != null) {
                    Message message = (Message) packet;
                    if (message.getBody() != null) {
                        final String roomName = StringUtils.parseName(message.getFrom());
                        final String from = StringUtils.parseResource(message.getFrom());
                        final String msg = message.getBody();
                        Log.d(TAG, "MUC PacketListener.processPacket() Receive room : " + roomName + " / msg : " + msg + " / from : " + from);
                        _handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (_listener != null)
                                    _listener.onChatReceiveRoomMessage(roomName, from, msg);
                            }
                        });
                    }
                }
            }
        }
    };

    /**
     * @return new boolean[] { isSuccess, isCreate }
     * <li>isSuccess : 방 연결이나 생성 성공</li>
     * <li>isCreate : 방을 생성하였으면 true</li>
     */
    public boolean[] createOrJoinRoom(String roomName, String password) throws NoResponseException, XMPPErrorException, SmackException, Exception {
        leaveJoinedRoom();
        boolean isSuccess = false;
        boolean isCreate = false;
        _muc = new MultiUserChat(_connection, getRoomJid(roomName));
        try {
            isCreate = _muc.createOrJoin(_userName);
//            _muc.sendConfigurationForm(new Form(Form.TYPE_SUBMIT));
            /*isCreate = _muc.createOrJoin(_userName);
            if (isCreate) {
                
                // Get the the room's configuration form
                Form form = _muc.getConfigurationForm();
                // Create a new form to submit based on the original form
                Form submitForm = form.createAnswerForm();
                // Add default answers to the form to submit
                for (FormField field : form.getFields()) {
                    if (!FormField.TYPE_HIDDEN.equals(field.getType()) && field.getVariable() != null) {
                        // Sets the default value as the answer
                        submitForm.setDefaultAnswer(field.getVariable());
                    }
                }
                // Set that the room requires a password
                submitForm.setAnswer("muc#roomconfig_passwordprotectedroom", true);
                // Set the password for the room
                submitForm.setAnswer("muc#roomconfig_roomsecret", "password");
                // Send the completed form (with default values) to the server to configure the room
                _muc.sendConfigurationForm(submitForm);
            }*/

            _muc.addMessageListener(_defaultMucPacketListener);
            _joinRoomName = roomName;
            _joinRoomNamePw = password;
            isSuccess = true;
        } catch (NoResponseException e) {
            Log.e(TAG, e.getMessage(), e);
            throw e;
        } catch (XMPPErrorException e) {
            Log.e(TAG, e.getMessage(), e);
            throw e;
        } catch (SmackException e) {
            Log.e(TAG, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            throw e;
        }
        return new boolean[]{isSuccess, isCreate};
    }

    /**
     * @return new boolean[] { isSuccess, isCreate }
     * <li>isSuccess : 방 연결이나 생성 성공</li>
     * <li>isCreate : 방을 생성하였으면 true</li>
     */
    public boolean[] createOrJoinRoom(String roomName, String passsword, PacketListener packetListener) throws NoResponseException, XMPPErrorException, SmackException, Exception {
        boolean[] isResult = createOrJoinRoom(roomName, passsword);
        if (isResult[0]) {
            _customMucPacketListener = packetListener;
            _muc.addMessageListener(_customMucPacketListener);
        }
        return isResult;
    }

    public boolean inviteRoom(String user) throws NotConnectedException, Exception {
        boolean isSuccess = false;
        try {
            _muc.invite(user, null);
            isSuccess = true;
        } catch (NotConnectedException e) {
            Log.e(TAG, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            throw e;
        }
        return isSuccess;
    }

    public boolean joinRoom(final String roomName, String password) throws NoResponseException, XMPPErrorException, SmackException, Exception {
        boolean isSuccess = false;
        leaveJoinedRoom();
        _muc = new MultiUserChat(_connection, getRoomJid(roomName));
        try {
            _muc.join(_userName, password);
            _muc.addMessageListener(_defaultMucPacketListener);
            _joinRoomName = roomName;
            _joinRoomNamePw = password;
            isSuccess = true;
        } catch (NoResponseException e) {
            Log.e(TAG, e.getMessage(), e);
            throw e;
        } catch (XMPPErrorException e) {
            Log.e(TAG, e.getMessage(), e);
            throw e;
        } catch (SmackException e) {
            Log.e(TAG, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            throw e;
        }
        return isSuccess;
    }

    public boolean joinRoom(String roomName, String password, PacketListener packetListener) throws NoResponseException, XMPPErrorException, SmackException, Exception {
        boolean isSuccess = joinRoom(roomName, password);
        if (isSuccess && packetListener != null) {
            _customMucPacketListener = packetListener;
            _muc.addMessageListener(_customMucPacketListener);
        }
        return isSuccess;
    }

    public void leaveJoinedRoom() {
        if (_muc != null) {
            try {
                _muc.removeMessageListener(_defaultMucPacketListener);
                if (_customMucPacketListener != null) {
                    _muc.removeMessageListener(_customMucPacketListener);
                }
                _muc.leave();
            } catch (NotConnectedException e) {
                Log.e(TAG, e.getMessage(), e);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
            _muc = null;
        }
        _joinRoomName = null;
    }

    public void sendRoomMsg(String msg) {
        boolean isSuccess = false;
        if (_muc != null) {
            try {
                _muc.sendMessage(msg);
                Log.i(TAG, "send room msg : " + _muc.getRoom() + " : " + msg);
                isSuccess = true;
            } catch (Exception e) {
                Log.e(TAG, e.getMessage() + " / fail send room msg : " + _muc.getRoom() + " : " + msg, e);
            }
        }
        if (_listener != null) _listener.onChatSendRoomMessage(isSuccess, _joinRoomName, msg);
    }

    protected List<HostedRoom> getHosedRooms() {
        List<HostedRoom> hostedRooms = new ArrayList<HostedRoom>();
        List<String> serviceName = new ArrayList<String>();
        ServiceDiscoveryManager discoverManager = ServiceDiscoveryManager.getInstanceFor(_connection);
        try {
            DiscoverItems items = discoverManager.discoverItems(HOST);
            for (DiscoverItems.Item item : items.getItems()) {
                Log.d(TAG, "DiscoverItems.Item getName() : " + item.getName() + " / getEntityID() : " + item.getEntityID());
                if (item.getEntityID().startsWith("conference") || item.getEntityID().startsWith("private")) {
                    serviceName.add(item.getEntityID());
                } else {
                    DiscoverInfo info = discoverManager.discoverInfo(item.getEntityID());
                    if (info.containsFeature("")) {
                        serviceName.add(item.getEntityID());
                    }
                }
            }
            for (String service : serviceName) {
                Collection<HostedRoom> rooms = MultiUserChat.getHostedRooms(_connection, service);
                for (HostedRoom room : rooms) {
                    hostedRooms.add(room);
                    Log.d(TAG, "HostedRoom getJid() : " + room.getJid());
                    RoomInfo roomInfo = MultiUserChat.getRoomInfo(_connection, room.getJid());
                    if (roomInfo != null) {
                        Log.d(TAG, "HostedRoom RoomInfo getOccupantsCount() : " + roomInfo.getOccupantsCount());
                    }
                }

            }
        } catch (NoResponseException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (XMPPErrorException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (NotConnectedException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return hostedRooms;
    }


    public final XMPPConnection getXmppConnection() {
        return _connection;
    }


    public interface OnChatListener {
        /**
         * 로그인이 완료되면 호출
         *
         * @param isSuccess 로그인 성공이면 true
         */
        void onChatCompleteLogin(boolean isSuccess);

        /**
         * 1:1 메시지 전송 완료 후 호출
         */
        void onChatSendMessage(boolean isSuccess, String to, String msg);

        /**
         * 1:1 메시지를 받으면 호출
         */
        void onChatReceiveMessage(String from, String msg);

        /**
         * 그룹 메시지 전송 완료 후 호출
         */
        void onChatSendRoomMessage(boolean isSuccess, String roomName, String msg);

        /**
         * 그룹 메시지를 받으면 호출
         */
        void onChatReceiveRoomMessage(String roomName, String from, String msg);

        /**
         * 로그인 시도시에 호출
         */
        void onChatConnectionAuthenticated();

        /**
         * {@link XMPPConnection} 재연결 성공시 호출
         *
         * @param isSuccess 재연결 성공 true, 실패 true
         * @param e         재연결 실패 Exception
         */
        void onChatReconnection(boolean isSuccess, Exception e);

        /**
         * {@link XMPPConnection} 연결 종료시 호출
         *
         * @param isSuccess 연결 정상 종료 true, 비정상 종료 false
         * @param e         재연결 실패 Exception
         */
        void onChatConnectionClosed(boolean isSuccess, Exception e);
    }


    /**
     * log XMPPConnection
     */
    public final void logXMPPConnection(XMPPConnection connection) {
        if (!DEBUG) return;
        Log.i(TAG, "--------------------------------------");
        Log.i(TAG, "XMPPConnection getHost : " + connection.getHost());
        Log.i(TAG, "XMPPConnection getPort : " + connection.getPort());
        Log.i(TAG, "XMPPConnection getServiceName : " + connection.getServiceName());
        Log.i(TAG, "XMPPConnection getConnectionCounter : " + connection.getConnectionCounter());
        Log.i(TAG, "XMPPConnection getConnectionID : " + connection.getConnectionID());
        Log.i(TAG, "XMPPConnection getPacketReplyTimeout : " + connection.getPacketReplyTimeout());
        Log.i(TAG, "XMPPConnection getServiceCapsNode : " + connection.getServiceCapsNode());
        Log.i(TAG, "XMPPConnection getFromMode : " + connection.getFromMode());

        Log.i(TAG, "XMPPConnection isConnected : " + connection.isConnected());
        Log.i(TAG, "XMPPConnection isAnonymous : " + connection.isAnonymous());
        Log.i(TAG, "XMPPConnection isAuthenticated : " + connection.isAuthenticated());
        Log.i(TAG, "XMPPConnection isRosterVersioningSupported : " + connection.isRosterVersioningSupported());
        Log.i(TAG, "XMPPConnection isSecureConnection : " + connection.isSecureConnection());
        Log.i(TAG, "XMPPConnection isUsingCompression : " + connection.isUsingCompression());
        Log.i(TAG, "--------------------------------------");
    }

    /**
     * log roster entry
     */
    public final void logRoster(Roster roster) {
        if (!DEBUG) return;
        if (roster != null) {
            Log.i(TAG, "--------------------------------------");
            Log.i(TAG, "Roster getEntries : " + roster.getEntries());
            Log.i(TAG, "Roster getEntryCount : " + roster.getEntryCount());
            Log.i(TAG, "Roster getGroups : " + roster.getGroups());
            Log.i(TAG, "Roster getGroupCount : " + roster.getGroupCount());
            Log.i(TAG, "Roster getSubscriptionMode : " + roster.getSubscriptionMode());
            Log.i(TAG, "Roster getDefaultSubscriptionMode : " + Roster.getDefaultSubscriptionMode());
            Log.i(TAG, "Roster getUnfiledEntries : " + roster.getUnfiledEntries());
            Log.i(TAG, "Roster getUnfiledEntryCount : " + roster.getUnfiledEntryCount());
            Log.i(TAG, "--------------------------------------");

            Collection<RosterEntry> entries = roster.getEntries();
            for (RosterEntry entry : entries) {
                Log.d(TAG, "--------------------------------------");
                Log.d(TAG, "RosterEntry : " + entry);
                Log.d(TAG, "RosterEntry getUser : " + entry.getUser());
                Log.d(TAG, "RosterEntry getName : " + entry.getName());
                Log.d(TAG, "RosterEntry getStatus : " + entry.getStatus());
                Log.d(TAG, "RosterEntry getType : " + entry.getType());

                Presence entryPresence = roster.getPresence(entry.getUser());
                Log.d(TAG, "Presence Status: " + entryPresence.getStatus());
                Log.d(TAG, "Presence Type: " + entryPresence.getType());
                Presence.Type type = entryPresence.getType();
                if (type == Presence.Type.available) {
                    Log.d(TAG, "Presence AVIALABLE");
                }
                Log.d(TAG, "Presence : " + entryPresence);
                Log.d(TAG, "--------------------------------------");
            }
        }
    }

}