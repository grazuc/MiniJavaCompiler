///[Error:met|7]


class A {

    void met() {
        var x = B.met("", new B());
    }
}

class B {

    static int met(String x, A a) {

    }
}