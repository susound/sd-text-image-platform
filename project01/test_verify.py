import requests, json, base64, time

data = {
    "prompt": "a cute cat sitting on a table, high quality, detailed",
    "negative_prompt": "blurry, low quality",
    "width": 768,
    "height": 768,
    "steps": 10,
    "cfg_scale": 7.0,
    "seed": 42
}
t0 = time.time()
r = requests.post("http://127.0.0.1:5000/generate", json=data, timeout=300)
elapsed = time.time() - t0
print(f"Status: {r.status_code}, time: {elapsed:.1f}s")
resp = r.json()
print(f"Success: {resp.get('success')}")
print(f"Seed: {resp.get('seed')}")
img_len = len(resp.get("image_base64") or "")
print(f"Image base64 length: {img_len}")
if img_len > 100:
    with open("test_final.png", "wb") as f:
        f.write(base64.b64decode(resp["image_base64"]))
    print("Saved to test_final.png")
elif not resp.get("success"):
    print(f"Error: {resp.get('error_message')}")
