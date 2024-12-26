
//test de clase que implementa una interface que extiende a otra

class A implements B {

    String x;

    int metodo_b() {

    }

    int metodo_c() {

    }

    static void main() {

    }

}


interface B extends C {

    int metodo_b();

}

interface C {

    int metodo_c();

}