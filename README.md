# JunkRecorder
A new Project for ITSP Workshop on Software Development

## 概要(2016/7/17更新)
その場で思いついたものをとりあえずメモしていくためのAndroidアプリ。
メモはJunkとしてAndroid内のローカルディレクトリ内にJson形式で保存される(拡張子はtxt)。
入力や出力といった画面はFragmentにより構成されており、ViewPagerを用いて切り替えができるようになっている。
現状ではメモを簡単に残し、落書きレベルの絵を描き、位置情報を記録することで、マップにプロットすることができる程度の機能を搭載している。

### 利用したもの
- ViewPager
-- ViewPagerの構造にあたってArrayPagerAdapter(https://github.com/takaaki7/ArrayPagerAdapter )を利用させていただきました。感謝の気持ちを込めてこちらに記します。
- GoogleMap
-- Android用のGoogleのAPIを利用しました。GitHub上ではAPIキーを除いていますので、このアプリの(MAP機能を用いた)起動にはAPIキーをapi_key.xmlに書き込む必要があります。
