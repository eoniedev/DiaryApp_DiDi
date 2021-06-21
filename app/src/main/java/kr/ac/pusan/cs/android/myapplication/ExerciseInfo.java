package kr.ac.pusan.cs.android.myapplication;

public class ExerciseInfo {
    public String event, time;

    public ExerciseInfo(String event, String hour, String minute) {
        this.event = event;
        this.time = hour + "시간 " + minute + "분";
    }
}
