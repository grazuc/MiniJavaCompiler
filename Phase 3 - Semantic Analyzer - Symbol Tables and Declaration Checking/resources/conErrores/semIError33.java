///[Error:Y|13]

//test herencia circular entre interfaces

class A {
    static void main(){}
}

interface X extends Z{}

interface Y extends X{}

interface Z extends Y{}
