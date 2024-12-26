///[Error:Z|10]

//herencia circular, el error podria ser cualquiera de las interfaces

class A extends B{
}

interface C extends Y{}
interface Y extends H{}
interface H extends Z{}
interface Z extends X{}
interface X extends C{}

class B implements H{
    static void main(){}
}
