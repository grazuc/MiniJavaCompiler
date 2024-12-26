///[Error:x|7]
//llamada a metodo con  parametros incompatibles

class A {

    void met() {
        var x = this.x('a');
    }

    A x(String x) {
        var a = new A();
        return a;
    }
    static void main(){}
}