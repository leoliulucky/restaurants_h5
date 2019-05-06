<script src="/js/rsa/RSA.min.js" type="text/javascript"></script>
<script src="/js/rsa/BigInt.min.js" type="text/javascript"></script>
<script src="/js/rsa/Barrett.min.js" type="text/javascript"></script>
<script type="text/javascript">
function encodeData(e, m, r, v){setMaxDigits(130);var key = new RSAKeyPair(e,"",m);return encryptedString(key, r+v);}
</script>
