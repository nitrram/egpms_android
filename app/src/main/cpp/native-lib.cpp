#include "jni_utils.h"

#include "egctl/egctl.c"

#include <string>

#define PACKAGE_PATH "egpms/lol/wtf/egpms/"

#define APP_ACTION_LEFT 0x00
#define APP_ACTION_ON 0x01
#define APP_ACTION_OFF 0x02
#define APP_ACTION_TOGGLE 0x04

#define LOG_TAG "native-lib"


// TODO Config will be created based upon app's input
static Config createDummyConfig(const std::string &s);

static Action commandToAction(jint command, size_t idx);

static jint convertState(Status st);

static std::string _config_line;

void mm_initConfig(
        JNIEnv *env,
        jobject  */*this*/,
        jstring config_line
) {
    const char *cstr = env->GetStringUTFChars(config_line, NULL);
    _config_line = {cstr};
    env->ReleaseStringUTFChars(config_line, cstr);
}

jint mm_getStatus(
        JNIEnv */* env */,
        jobject /* this */) {

    int sock;
    Session sess;


    Config conf = createDummyConfig(_config_line);
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
        JNIEnv */* env */,
        jobject /* this */,
        jint abcd) {

    int sock;
    Config conf = createDummyConfig(_config_line);
    Session sess;

    sock = create_socket(&conf.addr);
    establish_connection(sock);
    sess = authorize(sock, conf.key);

    Status stat = recv_status(sock, sess, conf.proto);
    Actions actions {ACTION_LEFT,ACTION_LEFT,ACTION_LEFT,ACTION_LEFT};

    for (size_t i = 0; i < SOCKET_COUNT; i++) {
        Action action = commandToAction(abcd, i);
        if (action == ACTION_INVALID) {
            LOGE("invalid action for socket %zu %d", i + 1, abcd);
            return 0;
        }

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
        { "initConfig", "(Ljava/lang/String;)V", reinterpret_cast<void*>(&mm_initConfig)},
        { "getStatus", "()I", reinterpret_cast<void*>(&mm_getStatus)},
        { "setState", "(I)I", reinterpret_cast<void*>(&mm_setState)}
};

jint JNI_OnLoad(JavaVM *vm, void * /* reserved */) {

    std::string className { PACKAGE_PATH "ControlActivity"};

    ON_LOAD(vm, className.c_str(), method_table);
}

/********************************** private utils */
Config createDummyConfig(const std::string &s) {


    LOGI("dummy config: %s", s.c_str());

    char *line = new char[s.size() + 1];
    std::copy(s.begin(), s.end(), line);
    line[s.size()] = '\0';

    Config conf;

    conf.proto = consume_protocol(&line);
    conf.addr.sin_addr.s_addr = consume_ip_address(&line);
    conf.addr.sin_port = consume_tcp_port(&line);
    conf.key = consume_key(&line);
    conf.addr.sin_family = AF_INET;

    delete line;

    LOGI("dummy line consumed");

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

    LOGI("result %d", result);
    return result;
}

