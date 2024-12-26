///[Error:x|21]

//test nombre de atributo heredado repetido

class A {

    int x;

}

class B extends A {




    static void main () {}
}

class C extends B {

    String x;

}