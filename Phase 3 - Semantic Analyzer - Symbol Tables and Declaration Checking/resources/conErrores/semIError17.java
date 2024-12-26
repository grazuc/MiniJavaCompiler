///[Error:debugPrint|7]

//test de una clase que redefine de manera incorrecta el metodo debugPrint

class A implements B {

    void debugPrint(int x) {

    }

    static void main () {

    }

}

class B implements C {

}

interface C {

}