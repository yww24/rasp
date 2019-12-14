#include <sys/socket.h>
#include <stdlib.h>
#include <wiringPi.h>
#include <pthread.h>
#include <softTone.h>
#include <stdio.h>
#include <arpa/inet.h>
#include <sys/types.h>
#include <string.h>

#define PORT 8080
#define BUF_LEN 128

#define SPKR 6
#define trigPin 27
#define echoPin 28
#define LED 1
#define SW 5
#define CHECK 1
#define CLOSE 2
#define EXIT 0

int note = 440;
int distance = 0;
int musictrig;
int check=0;
int command_dis;

int closestack = 0;

pthread_mutex_t music_lock;
pthread_mutex_t ultra_lock;

pthread_t ptMusic, ptUltra, ptSwitch;


void *music(){
	while(1){
		if(check == 0 &&musictrig==1){
			softToneWrite(SPKR,note);
			delay(1000);
		}
		else if (musictrig == 0){
			softToneWrite(SPKR,0);
			delay(1000);
		}
	}
}

void *Ultra(){
	int i = 0;
	int pulse = 0;

	long startTime;
	long travelTime;



	while(1){

		digitalWrite(trigPin, LOW);
		usleep(20);
		digitalWrite (trigPin, HIGH);
		usleep(20);
		digitalWrite (trigPin, LOW);
		while(digitalRead(echoPin) == LOW);	// busy wait
		
		startTime = micros();
	
		while(digitalRead(echoPin) == HIGH);	// busy wait

		travelTime = micros() - startTime;
	
		distance = travelTime / 58;
		if(distance < command_dis && check == 0){
			musictrig = 1;
		}
		printf("musictrig=%d\n",musictrig);
		printf("check=%d\n",check);
		delay(100);

	}
	
}

void *switchfunction(){
	int i;

	while(1){
		i = digitalRead(SW);
		printf("digitalRead = %d\n",i);
		delay(100);
		if(digitalRead(SW) == 0){
			musictrig=0;
		}
	}
}

int main(){
	int server_sockfd, client_sockfd,command;
	char buf[BUF_LEN];
	struct sockaddr_in serveraddr, clientaddr;
	int client_len,chk_bind,read_len;
	char *ptr;
	

	client_len = sizeof(clientaddr);
	
	server_sockfd = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
	if(server_sockfd == -1){
		perror("socket error : ");
		exit(0);
	}

	bzero(&serveraddr, sizeof(serveraddr));
	serveraddr.sin_family = AF_INET;
	serveraddr.sin_addr.s_addr = htonl(INADDR_ANY);				// 옵션설정 (IP 주소- 받는 것이므로 특정 안함)
	serveraddr.sin_port = htons(PORT);								// 옵션설정 (포트 번호)
	chk_bind = bind(server_sockfd, (struct sockaddr *)&serveraddr, sizeof(serveraddr));	// 소켓과 소켓 구조체 연결
	if(chk_bind > 0) {
		perror("bind error : ");
		exit(0);
	}		

	if(listen(server_sockfd,5)){
		perror("listen error : ");
	}

	client_sockfd = accept(server_sockfd, (struct sockaddr *)&clientaddr, &client_len);

	printf("New Client Connect: %s\n", inet_ntoa(clientaddr.sin_addr));

	memset(buf,0x00,BUF_LEN);
	read_len = read(client_sockfd,buf,BUF_LEN);

	command_dis = atoi(buf);

	softToneCreate(SPKR);
	pinMode(trigPin,OUTPUT);
	pinMode(echoPin,INPUT);
	pinMode(SW, INPUT);
	
	if(wiringPiSetup()==-1){
		printf("Unable GPIO SETUP");
		return 1;
	}

	pthread_create(&ptSwitch, NULL, switchfunction, NULL);
	pthread_create(&ptUltra, NULL, Ultra,NULL);
	pthread_create(&ptMusic,NULL, music, NULL);



	while(1) {
		if(musictrig==1){
			memset(buf,0x00,BUF_LEN);
			write(client_sockfd,"warning",BUF_LEN);
		}
		memset(buf, 0x00, BUF_LEN);
		read_len = read(client_sockfd, buf, BUF_LEN);
		
		command = atoi(buf);

		switch(command) {
			case CHECK :
				check = 1;
				printf("checked\n");
				break;
			case CLOSE :
				check = 0;
				printf("closed\n");
				break;
			case EXIT :
				printf("EXIT\nbye!");
				return 0;
			default : 
				printf("Invalid command %d\n", command);
		}
	}

	pthread_join(ptSwitch, NULL);
	pthread_join(ptMusic,NULL);
	pthread_join(ptUltra,NULL);
	
	pthread_mutex_destroy(&music_lock);
	pthread_mutex_destroy(&ultra_lock);
}
