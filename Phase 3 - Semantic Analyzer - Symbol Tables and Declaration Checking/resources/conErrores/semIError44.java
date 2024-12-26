///[Error:met|10]


class A {
    void met(){}
}

class B extends A {
    static void main() {}
    ClaseC met() {}

    // el error me dice podria ser que el metodo esta mal redefinido o que ClaseC no esta definida
}