import requests, json, base64, time

t0 = time.time()
r = requests.post("http://127.0.0.1:5000/generate", json={
    "prompt": "a cute cat sitting on a table, high quality",
    "negative_prompt": "blurry, low quality",
    "width": 768,
    "height": 768,
    "steps": 10,
    "cfg_scale": 7.0,
    "seed": 42
}, timeout=300)
elapsed = time.time() - t0
print(f"Status: {r.status_code}, time: {elapsed:.1f}s")
data = r.json()
print(f"Finished: {data.get('finished')}")
print(f"Seed: {data.get('seed')}")
img_len = len(data.get("images", [""])[0]) if "images" in data else 0
print(f"Image base64 length: {img_len}")
if img_len > 100:
    with open("test_sdxl_output.png", "wb") as f:
        f.write(base64.b64decode(data["images"][0]))
    print("Saved to test_sdxl_output.png")
