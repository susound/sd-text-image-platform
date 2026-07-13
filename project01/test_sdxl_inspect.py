import requests
r = requests.post("http://127.0.0.1:5000/generate", json={
    "prompt": "a cute cat",
    "negative_prompt": "",
    "width": 768, "height": 768, "steps": 10, "cfg_scale": 7.0, "seed": 42
}, timeout=300)
print("Status:", r.status_code)
data = r.json()
print("Keys:", list(data.keys()))
for k, v in data.items():
    if isinstance(v, list):
        print(f"{k}: list length {len(v)}")
        if len(v) > 0:
            item = v[0]
            print(f"  first item type: {type(item).__name__}, len: {len(item) if isinstance(item, str) else 'N/A'}")
    elif isinstance(v, str):
        s = v[:80]
        print(f"{k}: str len {len(v)} = {s}")
    else:
        print(f"{k}: {v}")
