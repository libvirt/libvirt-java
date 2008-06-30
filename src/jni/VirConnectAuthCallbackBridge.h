#include <jni.h>
#include <libvirt/libvirt.h>

typedef struct {
	JNIEnv *env;
	jobject auth;
} CallBackStructType;

int	VirConnectAuthCallbackBridge(virConnectCredentialPtr cred, unsigned int ncred, void * cbdata);