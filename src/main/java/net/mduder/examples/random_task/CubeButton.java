package net.mduder.examples.random_task;

/**
 * This class used to extend Button.  However, No amount of tweaking could get it to render/draw
 * on a Samsung Note 3 (with Kitkat) so the class now only contains model logic.
 */
public class CubeButton {
    enum Direction { LEFT, UP, RIGHT, DOWN }

    private int face_left, face_right, face_up, face_down, face_reverse;
    private int face_value = 0;

    public int getFaceValue() {
        return face_value;
    }

    public void setFaceValue(int faceValue) {
        if (faceValue < 1 || faceValue > 6) {
            return;
        }

        switch (faceValue) {
            case 1:
                face_left = 2;
                face_up = 3;
                break;
            case 2:
                face_left = 3;
                face_up = 1;
                break;
            case 3:
                face_left = 1;
                face_up = 2;
                break;
            case 4:
                face_left = 5;
                face_up = 6;
                break;
            case 5:
                face_left = 6;
                face_up = 4;
                break;
            case 6:
                face_left = 4;
                face_up = 5;
                break;
            default:
                return;
        }
        face_value = faceValue;
        face_reverse = 7 - faceValue;
        face_right = 7 - face_left;
        face_down = 7 - face_up;
    }

    public void rotate(Direction direction) {
        if (this.face_value == 0) {
            return;
        }

        int temp = face_value;
        switch (direction) {
            case LEFT:
                face_value = face_right;
                face_right = face_reverse;
                face_reverse = face_left;
                face_left = temp;
                break;
            case UP:
                face_value = face_down;
                face_down = face_reverse;
                face_reverse = face_up;
                face_up = temp;
                break;
            case RIGHT:
                face_value = face_left;
                face_left = face_reverse;
                face_reverse = face_right;
                face_right = temp;
                break;
            case DOWN:
                face_value = face_up;
                face_up = face_reverse;
                face_reverse = face_down;
                face_down = temp;
                break;
        }
    }

    public CubeButton () {
        setFaceValue(1);
    }
}
