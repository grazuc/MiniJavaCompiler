
class A {

    int met() {
        this.met2();
        return 3;
    }

    B met2() {
        var x = this.met2();
        return x;
    }
}

class B {
    static void main(){}
}