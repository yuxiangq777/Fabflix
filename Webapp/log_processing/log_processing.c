#include<stdio.h>
#include <stdlib.h>
#include <string.h>

struct room{
    char number[10];
    char des[80];
    char north_number[10];
    char south_number[10];
    char east_number[10];
    char west_number[10];
};
struct room str_to_room(char * line){
    struct room ans;
    strcpy(ans.number,strtok(line, " +"));
    strcpy(ans.des,strtok(NULL, "+"));
    strcpy(ans.north_number,strtok(NULL, " "));
    strcpy(ans.south_number,strtok(NULL, " "));
    strcpy(ans.east_number,strtok(NULL, " "));
    strcpy(ans.west_number,strtok(NULL, " "));
    return ans;
}
void chomp(char *s) {
    while(*s && *s != '\n' && *s != '\r') s++;

    *s = 0;
}
void replace_space(char *src)
{
    char *d = src;
    while (*src != '\0'){
        while (*src == ' ' && *(src + 1) == ' ')
            src++;
        *d++ = *src++;
    }
    *d = '\0';
}
int main() {
    int ts=0;
    int tj=0;
    int count=0;
    FILE *fp;
    char line[120];
    if (fp = fopen("log.txt", "r")) {
        while (fgets(line, sizeof(line), fp) != NULL) {
            chomp(line);
            replace_space(line);
            char *a = strtok(line, ";");
            char *b = strtok(NULL, " ");
            ts+= atoi(a);
            tj+= atoi(b);
            count+=1;
        }
        fclose(fp);
    }
    double t1= (double) ts/ (double) count;
    double t2= (double) tj/ (double) count;
    printf("Average TS:%fms. Average TJ:%fms",t1,t2);
    return 0;
}