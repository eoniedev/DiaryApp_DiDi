package kr.ac.pusan.cs.android.myapplication;

public class Todolistitem {
    private String todotext;
    private int checked;
    public String getTodotext() {
        return this.todotext;
    }
    public void setTodotext(String text) {
        todotext = text;
    }
    public int getchecked() {
        return this.checked;
    }
    public void setchecked(int check) {
        checked = check;
    }
}
