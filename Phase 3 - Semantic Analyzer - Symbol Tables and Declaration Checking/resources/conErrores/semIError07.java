///[Error:met|13]

//test nombre de metodo repetido dentro de una misma clase

class A {

    int x;

    void met() {

    }

    int met() {

    }

}

class B extends A {

    char y;
    static void main() {}
}