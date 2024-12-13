#version 150

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;
uniform sampler2D Sampler2;
uniform sampler2D Sampler3;
uniform sampler2D Sampler4;
uniform sampler2D Sampler5;

uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

uniform mat4 ProjMat;
uniform mat4 ModelViewMat;


in float vertexDistance;
in vec4 vertexColor;
in vec2 texCoord0;

out vec4 fragColor;

void main() {
	//***********    Basic setup    **********

	// Normalized pixel coordinates (from 0 to 1)
	vec2 uv = gl_FragCoord.xy/vertexDistance/10.0;
	// Position of fragment relative to centre of screen
	vec2 pos = 0.5 - uv;
	// Adjust y by aspect for uniform transforms
	pos.y /= vertexDistance/vertexDistance;

	//**********         Glow        **********

	// Equation 1/x gives a hyperbola which is a nice shape to use for drawing glow as
	// it is intense near 0 followed by a rapid fall off and an eventual slow fade
	float dist = 1.0/length(pos);

	//**********        Radius       **********

	// Dampen the glow to control the radius
	dist *= 300;

	//**********       Intensity     **********

	// Raising the result to a power allows us to change the glow fade behaviour
	// See https://www.desmos.com/calculator/eecd6kmwy9 for an illustration
	// (Move the slider of m to see different fade rates)
	dist = pow(dist, 0.8);

	// Knowing the distance from a fragment to the source of the glow, the above can be
	// written compactly as:
	//	float getGlow(float dist, float radius, float intensity){
	//		return pow(radius/dist, intensity);
	//	}
	// The returned value can then be multiplied with a colour to get the final result

	// Add colour
	vec3 col = dist * vec3(1.0);

	// Tonemapping. See comment by P_Malin
	col = 1.0 - exp( -col );

	// Output to screen
	fragColor = vec4(col, 1.0);
}
