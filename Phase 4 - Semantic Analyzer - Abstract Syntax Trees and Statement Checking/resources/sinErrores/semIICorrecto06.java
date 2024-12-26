class A extends B {

    String y;

    void met() {
        var x = this.y(y);
    }

    B y(String x) {
        return new B();
    }
}

class B {
    static void main() {}
}