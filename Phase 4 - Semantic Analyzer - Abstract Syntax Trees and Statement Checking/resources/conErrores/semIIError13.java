///[Error:x|7]
//llamada a metodo con incorrecta cantidad de parametros

class A {

    void met() {
        var x = this.x();
    }

    A x(String x) {
        return new A();
    }
    static void main(){}
}