///[Error:m1|7]
class A {

    int y;

    void met() {
        this.m1(y);
    }
    void m1(boolean x) {
        x = ((4 + 10 + 33) > 12) || ((99 * 15) == 23) && (97 <= 12) || !true && ((4 / 2) != 2);
        x = false;
    }
    static void main(){}
}