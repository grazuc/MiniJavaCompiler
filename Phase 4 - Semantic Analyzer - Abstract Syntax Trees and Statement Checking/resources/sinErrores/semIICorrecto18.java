
class X {

    X x;

    int m() {
        if (true) {
            return this.m2(9);
        }
        else
            return this.x.m3();
    }

    int m2(int x) {
        var y = 99 - x;
        return this.x.m() * y;
    }

    int m3() {
        var z = this.x.m() + 3;
        var y = z;
        return y;
    }

    static void main(){}
}