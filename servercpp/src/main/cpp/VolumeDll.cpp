#include <windows.h>
#include <mmdeviceapi.h>
#include <endpointvolume.h>
#include "com_meandmyphone_server_services_impl_VolumeService.h"
#include "com_meandmyphone_server_services_impl_MouseService.h"
#include "com_meandmyphone_server_services_impl_KeyboardService.h"
#include "VolumeDll.h"
#include <jni.h>
#include <iostream>
#include <vector>

#define DLLExport __declspec(dllexport)
extern "C"
{

	typedef struct _JNI_POINT {
		jclass cls;
		jmethodID constructortorID;
		jfieldID x;
		jfieldID y;
	} JNI_POINT;	

	JNI_POINT* jni_point = NULL;
	jint previousVolume = -1;

	JNIEXPORT jint JNICALL Java_com_meandmyphone_server_services_impl_VolumeService_getVolume(JNIEnv*, jobject) {
		HRESULT hr;

		CoInitialize(NULL);
		IMMDeviceEnumerator* deviceEnumerator = NULL;

		hr = CoCreateInstance(__uuidof(MMDeviceEnumerator), NULL, CLSCTX_INPROC_SERVER, __uuidof(IMMDeviceEnumerator), (LPVOID*)&deviceEnumerator);

		IMMDevice* defaultDevice = NULL;
		hr = deviceEnumerator->GetDefaultAudioEndpoint(eRender, eConsole, &defaultDevice);
		deviceEnumerator->Release();
		deviceEnumerator = NULL;
		IAudioEndpointVolume* endpointVolume = NULL;
		hr = defaultDevice->Activate(__uuidof(IAudioEndpointVolume), CLSCTX_INPROC_SERVER, NULL, (LPVOID*)&endpointVolume);
		defaultDevice->Release();
		defaultDevice = NULL;
		float currentVolume = 0;
		hr = endpointVolume->GetMasterVolumeLevelScalar(&currentVolume);
		endpointVolume->Release();
		CoUninitialize();
		jint retval = (jint)(65535 * currentVolume);
		if (previousVolume != retval) {
			std::cout << "Volume change detected, current volume = " << retval << std::endl;
			previousVolume = retval;
		}

		return retval;
	}

	JNIEXPORT void JNICALL Java_com_meandmyphone_server_services_impl_VolumeService_setVolume(JNIEnv*, jobject, jint volume) {
		HRESULT hr;

		CoInitialize(NULL);
		IMMDeviceEnumerator* deviceEnumerator = NULL;
		hr = CoCreateInstance(__uuidof(MMDeviceEnumerator), NULL, CLSCTX_INPROC_SERVER, __uuidof(IMMDeviceEnumerator), (LPVOID*)&deviceEnumerator);
		IMMDevice* defaultDevice = NULL;

		hr = deviceEnumerator->GetDefaultAudioEndpoint(eRender, eConsole, &defaultDevice);
		deviceEnumerator->Release();
		deviceEnumerator = NULL;

		IAudioEndpointVolume* endpointVolume = NULL;
		hr = defaultDevice->Activate(__uuidof(IAudioEndpointVolume), CLSCTX_INPROC_SERVER, NULL, (LPVOID*)&endpointVolume);
		defaultDevice->Release();
		defaultDevice = NULL;

		float volumeNormalized = volume / (float)65535.0;

		hr = endpointVolume->SetMasterVolumeLevelScalar((float)volumeNormalized, NULL);

		endpointVolume->Release();

		CoUninitialize();
	}

	JNIEXPORT jobject JNICALL Java_com_meandmyphone_server_services_impl_MouseService_getMousePosition(JNIEnv* env, jobject) {
		LPPOINT point = new POINT;
		GetCursorPos(point);
		int x = point->x;
		int y = point->y;

		LoadJniPoint(env);

		jobject jPos_point = env->NewObject(jni_point->cls, jni_point->constructortorID);

		jint jix = (jint)x;
		env->SetIntField(jPos_point, jni_point->x, jix);

		jint jiy = (jint)y;
		env->SetIntField(jPos_point, jni_point->y, jiy);

		return jPos_point;
	}

	JNIEXPORT void JNICALL Java_com_meandmyphone_server_services_impl_MouseService_setMousePosition(JNIEnv*, jobject, jint x, jint y) {
		SetCursorPos(x, y);

	}

	JNIEXPORT void JNICALL Java_com_meandmyphone_server_services_impl_MouseService_clickMouse(JNIEnv*, jobject, jint button) {
		if (button == 0)
			mouse_event(MOUSEEVENTF_LEFTDOWN | MOUSEEVENTF_LEFTUP, 0, 0, 0, 0);
		else if (button == 1) {
			mouse_event(MOUSEEVENTF_RIGHTDOWN | MOUSEEVENTF_RIGHTUP, 0, 0, 0, 0);
		}
	}
	
	JNIEXPORT void JNICALL Java_com_meandmyphone_server_services_impl_MouseService_holdMouseButton(JNIEnv*, jobject, jint button) {
		if (button == 0)
			mouse_event(MOUSEEVENTF_LEFTDOWN, 0, 0, 0, 0);
		else if (button == 1) {
			mouse_event(MOUSEEVENTF_RIGHTDOWN | MOUSEEVENTF_RIGHTUP, 0, 0, 0, 0);
		}
	}

	JNIEXPORT void JNICALL Java_com_meandmyphone_server_services_impl_MouseService_releaseMouseButton(JNIEnv*, jobject, jint button) {
		if (button == 0)
			mouse_event(MOUSEEVENTF_LEFTUP, 0, 0, 0, 0);
		else if (button == 1) {
			mouse_event(MOUSEEVENTF_RIGHTUP, 0, 0, 0, 0);
		}
	}


	JNIEXPORT void JNICALL Java_com_meandmyphone_server_services_impl_KeyboardService_inputKeys(JNIEnv* env, jobject, jstring input) {
		INPUT ip;

		ip.type = INPUT_KEYBOARD;
		ip.ki.wScan = 0; 
		ip.ki.time = 0;
		ip.ki.dwExtraInfo = 0;

		HKL locale = GetKeyboardLayout(0);

		

		const char* nativeString = env->GetStringUTFChars(input, 0);

		if (0 == strcmp(nativeString, "enterKeycode")) {
			ip.ki.wVk = VK_RETURN;
			ip.ki.dwFlags = 0; // 0 for key press

			SendInput(1, &ip, sizeof(INPUT));
			ip.ki.dwFlags = KEYEVENTF_KEYUP; // KEYEVENTF_KEYUP for key release
			SendInput(1, &ip, sizeof(INPUT));
			return;
		}
		else if (0 == strcmp(nativeString, "backspaceKeycode")) {
			ip.ki.wVk = 0x08;
			ip.ki.dwFlags = 0; // 0 for key press

			SendInput(1, &ip, sizeof(INPUT));
			ip.ki.dwFlags = KEYEVENTF_KEYUP; // KEYEVENTF_KEYUP for key release
			SendInput(1, &ip, sizeof(INPUT));

			ip.ki.wVk = 0x2E;
			ip.ki.dwFlags = 0; // 0 for key press
			SendInput(1, &ip, sizeof(INPUT));
			ip.ki.dwFlags = KEYEVENTF_KEYUP; // KEYEVENTF_KEYUP for key release
			SendInput(1, &ip, sizeof(INPUT));


			return;
		}

		const size_t len = strlen(nativeString);
		char chars[100];

		strncpy_s(chars, nativeString, len);

		for (int i = 0; i < len; i++) {

			SHORT keyCode = VkKeyScanExA(chars[i], locale);
			bool shiftDown = false;
			bool ctrlDown = false;


			INPUT ctrlIp;
			INPUT shiftIp; 

			if (keyCode & 0x100) // check upper byte for shift flag
			{
				shiftDown = true;

				shiftIp.type = INPUT_KEYBOARD;
				shiftIp.ki.wScan = 0;
				shiftIp.ki.time = 0;
				shiftIp.ki.dwExtraInfo = 0;
				shiftIp.ki.wVk = 0xA0;
				shiftIp.ki.dwFlags = 0;
				SendInput(1, &shiftIp, sizeof(INPUT));
			}
			if (keyCode & 0x200) // check for ctrl flag
			{
				ctrlDown = true;

				ctrlIp.type = INPUT_KEYBOARD;
				ctrlIp.ki.wScan = 0;
				ctrlIp.ki.time = 0;
				ctrlIp.ki.dwExtraInfo = 0;
				ctrlIp.ki.wVk = 0xA2;
				ctrlIp.ki.dwFlags = 0;
				SendInput(1, &ctrlIp, sizeof(INPUT));
			}


			ip.ki.wVk = keyCode & 0xFF; 
			ip.ki.dwFlags = 0; // 0 for key press

			SendInput(1, &ip, sizeof(INPUT));
			ip.ki.dwFlags = KEYEVENTF_KEYUP; // KEYEVENTF_KEYUP for key release
			SendInput(1, &ip, sizeof(INPUT));

			if (shiftDown) {
				shiftIp.ki.dwFlags = KEYEVENTF_KEYUP;
				SendInput(1, &shiftIp, sizeof(INPUT));
			}

			if (ctrlDown) {
				ctrlIp.ki.dwFlags = KEYEVENTF_KEYUP;
				SendInput(1, &ctrlIp, sizeof(INPUT));
			}
		}

	}

	void LoadJniPoint(JNIEnv* env) {
		jni_point = new JNI_POINT;

		jni_point->cls = env->FindClass("com/meandmyphone/server/vo/Point");
		jni_point->constructortorID = env->GetMethodID(jni_point->cls, "<init>", "()V");
		jni_point->x = env->GetFieldID(jni_point->cls, "x", "I");
		jni_point->y = env->GetFieldID(jni_point->cls, "y", "I");
	}

}