"""Debug SDXL generation by importing main.py's logic and capturing full traceback."""
import sys
import traceback
import os
os.chdir(r"F:\Stable\project01\inference-service")
sys.path.insert(0, r"F:\Stable\project01\inference-service")

import main  # our inference service code

# Get pipeline
pipe = main.get_pipeline(None)
import torch
print(f"Pipeline type: {type(pipe).__name__}")
print(f"Pipe has device attr: {hasattr(pipe, 'device')}")
if hasattr(pipe, 'device'):
    print(f"Pipe device attr: {pipe.device}")
print(f"Pipe scheduler: {type(pipe.scheduler).__name__}")

# Test generation
gen_device = "cuda" if torch.cuda.is_available() else "cpu"
generator = torch.Generator(device=gen_device).manual_seed(42)

try:
    result = pipe(
        prompt="a cute cat sitting on a table, high quality",
        negative_prompt=None,
        width=768,
        height=768,
        num_inference_steps=10,
        guidance_scale=7.0,
        generator=generator,
    )
    print(f"Success! Images: {len(result.images)}")
    result.images[0].save("test_sdxl_debug.png")
    print("Saved to test_sdxl_debug.png")
except Exception:
    traceback.print_exc()
