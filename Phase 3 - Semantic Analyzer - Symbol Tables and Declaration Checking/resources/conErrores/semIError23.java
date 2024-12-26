///[Error:A|17]

//test de interface que extiende a una interface que esta declarada pero existe una clase con el mismo nombre que la interface

interface B extends A {

    void metodo_1();

    void metodo_2();

}

interface  A {

}

class A {

    void metodo_1() {

    }
    static void main(){}
}
