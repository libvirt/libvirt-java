#ifndef GENERIC_H_
#define GENERIC_H_
#include <jni.h>
#include <libvirt/libvirt.h>
#include <stdlib.h>

/* The macros here only make sense if they are the only thing in a function.
 */

/*
 * Generic macro with a VIROBJ ARGument returning an int
 * (for functions like virDomainFree)
 */
#define GENERIC__VIROBJ__INT(ENV, OBJ, VIROBJ1, VIRFUNC1)		\
	return (jint)VIRFUNC1(VIROBJ1);

/*
 * Generic macro with a VIROBJ and an int arguments returning an int
 * (for functions like virStorageVolDelete)
 */
#define GENERIC__VIROBJ_INT__INT(ENV, OBJ, VIROBJ1, ARG1, VIRFUNC1)	 \
	return (jint)VIRFUNC1(VIROBJ1, ARG1);

/*
 * Generic macro with a VIROBJ arguments returning a constant String
 * (for functions like virNetworkGetBridgeName	)
 */
#define GENERIC__VIROBJ__CONSTSTRING(ENV, OBJ, VIROBJ1, VIRFUNC1)	\
	jstring j_retstring=NULL;					\
	const char *retstring;						\
	if((retstring = VIRFUNC1(VIROBJ1))){				\
		j_retstring = (*ENV)->NewStringUTF(ENV, retstring);	\
	}								\
	return j_retstring;

/*
 * Generic macro with a VIROBJ arguments returning a String to be freed by
 * the caller (for functions like virNetworkGetName)
 */
#define GENERIC__VIROBJ__STRING(ENV, OBJ, VIROBJ1,VIRFUNC1)		\
	jstring j_retstring=NULL;					\
	char *retstring;						\
	if((retstring = VIRFUNC1(VIROBJ1))){				\
		j_retstring = (*ENV)->NewStringUTF(ENV, retstring);	\
		free(retstring);					\
	}								\
	return j_retstring;

/*
 * Generic macro with a VIROBJ and an int argument returning a String to be freed by the caller
 * (for functions like virStoragePoolGetXMLDesc)
 */
#define GENERIC_VIROBJ_INT__STRING(ENV, OBJ, VIROBJ, ARG1, VIRFUNC1)	\
	jstring j_retstring;						\
	char* retstring = NULL;						\
	if((retstring = VIRFUNC1(VIROBJ, ARG1))){			\
		j_retstring = (*ENV)->NewStringUTF(ENV, retstring);	\
		free(retstring);					\
	}								\
	return j_retstring;

/*
 * Generic macro with a VIROBJ and an String arguments returning an int
 * (for functions like virDomainDetachDevice )
 */
#define GENERIC_VIROBJ_STRING__INT(ENV, OBJ, VIROBJ, J_XMLDESC, VIRFUNC1) \
	const char *xmlDesc=(*ENV)->GetStringUTFChars(ENV, J_XMLDESC, NULL); \
	jint retval = (jlong)VIRFUNC1(VIROBJ, xmlDesc);			\
	(*ENV)->ReleaseStringUTFChars(ENV, J_XMLDESC, xmlDesc);		\
	return retval;

/*
 * Generic macro with a VIROBJ and an String arguments returning a virObject
 * (for functions like *CreateXML* that take no flags)
 */
#define GENERIC_VIROBJ_STRING__VIROBJ(ENV, OBJ, VIROBJ, J_XMLDESC, VIRFUNC1) \
	const char *xmlDesc=(*ENV)->GetStringUTFChars(ENV, J_XMLDESC, NULL); \
	jlong retval = (jlong)VIRFUNC1(VIROBJ, xmlDesc);		\
	(*ENV)->ReleaseStringUTFChars(ENV, J_XMLDESC, xmlDesc);		\
	return retval;

/*
 * Generic macro with a VIROBJ and String and int arguments returning a
 * virObject (for functions like *CreateXML* that take a flags)
 */
#define GENERIC_VIROBJ_STRING_INT__VIROBJ(ENV, OBJ, VIROBJ, J_XMLDESC, FLAGS, VIRFUNC1) \
	const char *xmlDesc=(*ENV)->GetStringUTFChars(ENV, J_XMLDESC, NULL); \
	jlong retval = (jlong)VIRFUNC1(VIROBJ, xmlDesc, FLAGS);		\
	(*ENV)->ReleaseStringUTFChars(ENV, J_XMLDESC, xmlDesc);		\
	return retval;


/*
 * Generic macro for the *getAutoStart functions
 */
#define GENERIC_GETAUTOSTART(ENV, OBJ, VIROBJ, VIRFUNC1)		\
	int autostart=0;						\
	VIRFUNC1(VIROBJ, &autostart);					\
	return (jboolean)autostart;

/*
 * Generic macro for the *getUUID functions
 */
#define GENERIC_GETUUID(ENV, OBJ, VIROBJ1, VIRFUNC1)			\
	unsigned char uuid[VIR_UUID_BUFLEN];				\
	jintArray j_uuid;						\
	int c;								\
	int uuidbyte;							\
	if(VIRFUNC1((void*)VIROBJ1, uuid)<0)				\
		return NULL;						\
	j_uuid=(*ENV)->NewIntArray(ENV, VIR_UUID_BUFLEN);		\
	for(c=0; c<VIR_UUID_BUFLEN; c++){				\
			uuidbyte=uuid[c];				\
	    (*ENV)->SetIntArrayRegion(ENV, j_uuid, c, 1, &uuidbyte);	\
	}								\
	return j_uuid;

/*
 * Generic macro for the *getUUIDString functions
 */
#define GENERIC_GETUUIDSTRING(ENV, OBJ, VIROBJ, VIRFUNC1)		\
	char uuidString[VIR_UUID_STRING_BUFLEN];			\
	VIRFUNC1(VIROBJ, uuidString);					\
	return (*ENV)->NewStringUTF(ENV, uuidString);


/*
 * Generic macro for the *List* functions that return an array of strings
 * VIRFUNC1 is the *List* function
 * VIRFUNC2 is the corresponding *NumOf* function
 */
#define GENERIC_LIST_STRINGARRAY(ENV, OBJ, VIROBJ,VIRFUNC1, VIRFUNC2)	\
	int maxnames;							\
	char **names;							\
	int c;								\
	jobjectArray j_names=NULL;					\
	if((maxnames = VIRFUNC2(VIROBJ))<0)				\
		return NULL;						\
	names= (char**)calloc(maxnames, sizeof(char*));			\
	if(VIRFUNC1(VIROBJ, names, maxnames)>=0){			\
		j_names= (jobjectArray)(*ENV)->NewObjectArray(ENV, maxnames, \
			(*ENV)->FindClass(ENV,"java/lang/String"),	\
			(*ENV)->NewStringUTF(ENV,""));			\
		for(c=0; c<maxnames; c++){				\
			(*ENV)->SetObjectArrayElement(ENV, j_names, c,	\
			(*ENV)->NewStringUTF(ENV, names[c]));		\
		}							\
	}								\
	free(names);							\
	return j_names;

/*
 * Generic macro for the *LookupBy* functions that take a string and return a VirObject
 */
#define GENERIC_LOOKUPBY_STRING(ENV, OBJ, VIROBJ, J_STRINGID, VIRFUNC1)	\
	const char *stringid=(*ENV)->GetStringUTFChars(ENV, J_STRINGID, NULL); \
	jlong retval = (jlong)VIRFUNC1(VIROBJ, stringid);		\
	(*ENV)->ReleaseStringUTFChars(ENV, J_STRINGID, stringid);	\
	return retval;

/*
 * Generic macro for the *LookupBy* functions that take no argument and return a VirObject
 */
#define GENERIC_LOOKUPBY_NONE(ENV, OBJ, VIROBJ, VIRFUNC1)		\
return (jlong)VIRFUNC1(VIROBJ);

/*
 * Generic macro for the *LookupBy* functions that take a UUID and return a VirObject
 */
#define GENERIC_LOOKUPBY_UUID(ENV, OBJ, VIROBJ, J_UUID, VIRFUNC1)	\
	unsigned char uuid[VIR_UUID_BUFLEN];				\
	int c;								\
	int *uuid_int  = (*ENV)->GetIntArrayElements(ENV, J_UUID, NULL);\
	for(c=0; c < VIR_UUID_BUFLEN; c++)				\
		uuid[c]=uuid_int[c];					\
	return (jlong)VIRFUNC1(VIROBJ, uuid);



#endif /*GENERIC_H_*/
