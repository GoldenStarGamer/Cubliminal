#version 150
#define SPEED 0.08

uniform sampler2D DiffuseSampler;
uniform vec2 OutSize;
uniform float RenderTime;

in vec2 texCoord;

out vec4 fragColor;


void main() {
    // distance from center of image, used to adjust blur
    vec2 uv = gl_FragCoord.xy / OutSize;
    float d = length(uv - vec2(0.5));

    // blur amount
    float blur = 0.25 * (1.0 + sin(RenderTime * SPEED * 2.0)) * (2.0 + sin(RenderTime * SPEED * 0.5));
    //blur = (1.0 + sin(RenderTime * 2.0 * SPEED)) * 0.5;
    //blur *= 1.0 + sin(RenderTime * 0.5 * SPEED) * 0.5;
    blur = pow(blur, 2.0);
    blur *= 0.05 * d;

    // final color
    vec4 col = texture(DiffuseSampler, texCoord);
    col.r = texture(DiffuseSampler, texCoord+vec2(blur,0.0)).r;
    col.b = texture(DiffuseSampler, texCoord-vec2(blur,0.0)).b;

    // vignette
    //col *= 1.0 - d * 0.5;

    fragColor = col;
}