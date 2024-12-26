///[Error:metodo_en_a|23]

// test de metodos heredados. La clase D "redefine" de manera incorrecta un metodo heredado de la clase A

class A {

    String metodo_en_a(){}
}

class B extends A {

    static void main() {}

}

class C extends B {


}

class D extends C {

    void metodo_en_a(){}
}