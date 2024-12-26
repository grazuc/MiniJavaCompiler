///[Error:m1|8]
//m1 es void y lo invoco para retornar un int
class A {

    int y;

    int metodo() {
        return this.m1(y);
    }

    void m1(String x){}
    static void main(){}
}