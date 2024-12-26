///[Error:=|8]

class A {
    X x;

    void metodo() {
        this.metodo();
        this.x = this.metodo();
    }
    static void main(){}
}

class X {

}
