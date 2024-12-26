class X {

    X x;

    X met() {
        this.met().x.met2();
    }

    int met2(){}

    static void main() {}
}