#include "jni_utils.h"

#include "egctl/egctl.c"

#include <string>

#define PACKAGE_PATH "lol/wtf/egpms/"

#define APP_ACTION_LEFT 0x00
#define APP_ACTION_ON 0x01
#define APP_ACTION_OFF 0x02
#define APP_ACTION_TOGGLE 0x04


// TODO Config will be created based upon app's input
static Config createDummyConfig();

static Action commandToAction(jint command, size_t idx);

static jint convertState(Status st);

jint mm_getStatus(
        JNIEnv *env,
        jobject /* this */) {

    int sock;
    Session sess;

    Config conf = createDummyConfig();

    sock = create_socket(&conf.addr);
    establish_connection(sock);
    sess = authorize(sock, conf.key);

    Status stat = recv_status(sock, sess, conf.proto);


    jint result = convertState(stat);
    close_session(sock);
    close(sock);


    return result;
}

jint mm_setState(
        JNIEnv *env,
        jobject /* this */,
        jint abcd) {

    int sock;
    Config conf = createDummyConfig();
    Session sess;

    sock = create_socket(&conf.addr);
    establish_connection(sock);
    sess = authorize(sock, conf.key);

    Status stat = recv_status(sock, sess, conf.proto);
    Actions actions {ACTION_INVALID,ACTION_INVALID,ACTION_INVALID,ACTION_INVALID};

    for (size_t i = 0; i < SOCKET_COUNT; i++) {
        Action action = commandToAction(abcd, i);
        if (action == ACTION_INVALID)
            fatal("Invalid action for socket %zu: %s", i+1, abcd >> (i*8));

        actions.socket[i] = action;
    }

    // side effect of triggering sockets
    Controls ctrl = construct_controls(stat, actions);
    send_controls(sock, sess, ctrl);


    stat = recv_status(sock, sess, conf.proto);
    jint result = convertState(stat);
    close_session(sock);
    close(sock);

    return result;

}

static JNINativeMethod method_table[] = {
        { "getStatus", "()I", reinterpret_cast<void*>(&mm_getStatus)},
        { "setStatus", "(I)I", reinterpret_cast<void*>(&mm_getStatus)}
};

jint JNI_OnLoad(JavaVM *vm, void * /* reserved */) {

    std::string className { PACKAGE_PATH "ControlActivity"};

    ON_LOAD(vm, className.c_str(), method_table);
}

/********************************** private utils */
Config createDummyConfig() {
    char *line = const_cast<char*>("egpms_local	   pms21    192.168.1.233    8000    pikachu");

    Config conf;
    conf.proto = consume_protocol(&line);
    conf.addr.sin_addr.s_addr = consume_ip_address(&line);
    conf.addr.sin_port = consume_tcp_port(&line);
    conf.key = consume_key(&line);
    conf.addr.sin_family = AF_INET;
    return conf;
}

Action commandToAction(jint command, size_t idx) {
    char b = static_cast<char>(command >> (idx * 8));

    switch(b) {
        case APP_ACTION_LEFT:
            return ACTION_LEFT;
        case APP_ACTION_ON:
            return ACTION_ON;
        case APP_ACTION_OFF:
            return ACTION_OFF;
        case APP_ACTION_TOGGLE:
            return ACTION_TOGGLE;
        default:
            return ACTION_INVALID;
    }
}

jint convertState(Status stat) {
    jint result = 0x0;
    for(int i=0;i< SOCKET_COUNT; ++i) {
        int bits = i*8;
        result ^= ((result >> bits) << bits);
        switch (stat.socket[i]) {
            case STATE_ON:
                result |= (0xff << bits);
                break;
            case STATE_ON_NO_VOLTAGE:
                result |= (0xf0 << bits);
                break;
            case STATE_OFF:
                break;
            case STATE_OFF_VOLTAGE:
                result |= (0xf << bits);
                break;
            default:
                break;
        }
    }
    return result;
}

