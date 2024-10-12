#version 150
#define AMPLITUDE 0.1
#define SPEED 0.1

uniform sampler2D DiffuseSampler;
uniform vec2 OutSize;
uniform float GameTime;
uniform float Time;

in vec2 texCoord;

out vec4 fragColor;
/*
vec4 vec4pow(vec4 i,float f) {
    vec4 j = pow(i, f);
    return vec4(j.rgb, i.w);
}
*/

void main() {
    vec2 uv = gl_FragCoord.xy / OutSize;
/*
    float amount = 0.0;

    amount = (1.0 + sin(Time*6.0)) * 0.5;
    amount *= 1.0 + sin(Time*16.0) * 0.5;
    amount *= 1.0 + sin(Time*19.0) * 0.5;
    amount *= 1.0 + sin(Time*27.0) * 0.5;
    amount = pow(amount, 3.0);

    amount *= 0.05;

    vec3 col;
    col.r = texture(DiffuseSampler, vec2(uv.x+amount,uv.y)).r;
    col.g = texture(DiffuseSampler, uv).g;
    col.b = texture(DiffuseSampler, vec2(uv.x-amount,uv.y)).b;

    col *= (1.0 - amount * 0.5);

    fragColor = vec4(col, texture(DiffuseSampler, texCoord).a);
*/
    //Color output starts at 0.
    vec4 c = vec4(0.0);

    //Iterate 20 times from 0 to 1
    for(float i = 0.; i<1.; i+=.05) {
        //Add a texture sample approaching the center (0.5, 0.5)
        //This center could moved to change how the direction of aberation
        //The mix amount determines the intensity of the aberration smearing
        //Note: .bgra is for blue tint here and isn't important to the example
        vec4 pv = texture(DiffuseSampler, mix(uv, vec2(.5), i*.2*cos(Time)));
        c += pv.brga*vec4(i,1.-abs(i+i-1.),1.-i,1)*.1;
        //This makes each sample have a different color from red to green to blue
        //The total should be multiplied by the 2/number of samples, (e.g. 0.1)
    }

    //Output the resulting color
    fragColor = c;
}